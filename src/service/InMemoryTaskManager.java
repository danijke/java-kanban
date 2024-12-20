package service;

import model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epicTasks = new HashMap<>();
    Queue<Integer> freeIds = new LinkedList<>();
    HistoryManager historyManager;
    private int counter = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic task = epicTasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask task = subtasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        if (subtasks.isEmpty()) {
            return null;
        } else {
            List<Subtask> epicSubtasks = new ArrayList<>();
            for (Subtask subtask : subtasks.values()) {
                if (epic.getId() == subtask.getEpicId()) {
                    epicSubtasks.add(subtask);
                }
            }
            return epicSubtasks.isEmpty() ? null : epicSubtasks;
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<Task> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Task> getAllTasks() {
        if (counter == 0) {
            return List.of();
        }
        List<Task> allTasks = new ArrayList<>();
        for (int i = 0; i <= counter; i++) {
            if (tasks.containsKey(i)) {
                allTasks.add(tasks.get(i));
            } else if (epicTasks.containsKey(i)) {
                allTasks.add(tasks.get(i));
            } else if (subtasks.containsKey(i)) {
                allTasks.add(tasks.get(i));
            }
        }
        return allTasks;
    }

    @Override
    public void addTask(Task task) {
        generateId(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic task) {
        generateId(task);
        epicTasks.put(task.getId(), task);
    }

    @Override
    public void addSubtask(Subtask task) {
        generateId(task);
        subtasks.put(task.getId(), task);
        if (task.getStatus() != TaskStatus.NEW) {
            calculateEpicStatus(task.getEpicId());
        }
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic task) {
        epicTasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask task) {
        subtasks.put(task.getId(), task);
        if (task.getStatus() != TaskStatus.NEW) {
            calculateEpicStatus(task.getEpicId());
        }
    }

    @Override
    public void clearTasks() {
        freeIds.addAll(tasks.keySet());
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        freeIds.addAll(epicTasks.keySet());
        freeIds.addAll(subtasks.keySet());
        epicTasks.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        freeIds.addAll(subtasks.keySet());
        subtasks.clear();
        if (!epicTasks.isEmpty()) {
            for (Epic epic : epicTasks.values()) {
                epic.setStatus(TaskStatus.NEW);
            }
        }
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        freeIds.offer(id);
    }

    @Override
    public void removeEpic(int id) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id) {
                int subtaskId = subtask.getId();
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        }
        epicTasks.remove(id);
        historyManager.remove(id);
        freeIds.offer(id);

    }

    @Override
    public void removeSubtask(int id) {
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        calculateEpicStatus(epicId);
        historyManager.remove(id);
        freeIds.offer(id);
    }

    void calculateEpicStatus(int epicId) {
        boolean allSubtasksNew = true;
        boolean allSubtasksDone = true;

        Epic epic = epicTasks.get(epicId);
        if (getEpicSubtasks(epic) == null) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        for (Subtask subtask : getEpicSubtasks(epic)) {
            TaskStatus status = subtask.getStatus();
            if (status == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            } else {
                if (status != TaskStatus.NEW) {
                    allSubtasksNew = false;
                } else {
                    allSubtasksDone = false;
                }
            }
        }
        if (allSubtasksNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allSubtasksDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    void generateId(Task task) {
        int id;
        if (!freeIds.isEmpty()) {
            id = freeIds.poll();
        } else {
            id = ++counter;
        }
        task.setId(id);
    }
}