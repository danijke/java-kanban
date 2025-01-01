package service;

import model.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    TreeSet<Task> sortedTasks = new TreeSet<>(Task.getComparator());
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
    public List<Subtask> getEpicSubtasks(int id) {
        return subtasks.values().stream()
                                .filter(subtask -> id == subtask.getEpicId())
                                .toList();
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

    public List<Task> getSortedTasks() {
        return Stream.of(tasks.values(), epicTasks.values(), subtasks.values())
                .flatMap(Collection::stream)
                .map(task -> (Task) task)
                .sorted(Comparator.comparingInt(Task::getId))
                .toList();
    }

    @Override
    public void addTask(Task task) {
        generateId(task);
        tasks.put(task.getId(), task);
        sortedTasks.add(task);
    }

    @Override
    public void addEpic(Epic task) {
        generateId(task);
        epicTasks.put(task.getId(), task);
        sortedTasks.add(task);
    }

    @Override
    public void addSubtask(Subtask task) {
        generateId(task);
        subtasks.put(task.getId(), task);
        sortedTasks.add(task);

        Epic epic = epicTasks.get(task.getEpicId());
        if (epic != null) {
            epic.addSubtask(task);
            if (task.getStatus() != TaskStatus.NEW) {
                calculateEpicStatus(task.getEpicId());
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.put(task.getId(), task);
        sortedTasks.remove(oldTask);
        sortedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic task) {
        Epic oldEpic = epicTasks.put(task.getId(), task);
        sortedTasks.remove(oldEpic);
        sortedTasks.add(task);
    }

    @Override
    public void updateSubtask(Subtask task) {
        int id = task.getId();
        Subtask old = subtasks.put(id, task);

        int epicId = task.getEpicId();
        Epic epic = epicTasks.get(epicId);
        if (epic != null) {
            epic.updateSubtask(old, task);
            if (task.getStatus() != TaskStatus.NEW) {
                calculateEpicStatus(epicId);
            }
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
            epicTasks.values().forEach(epic -> {
                epic.setStatus(TaskStatus.NEW);
                epic.clearSubtask();
            });
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
        getEpicSubtasks(id).forEach(subtask -> {
            int subtaskId = subtask.getId();
            subtasks.remove(subtaskId);
            freeIds.offer(subtaskId);
            historyManager.remove(subtaskId);
        });
        epicTasks.remove(id);
        historyManager.remove(id);
        freeIds.offer(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask task = subtasks.remove(id);
        int epicId = task.getEpicId();
        epicTasks.get(epicId).removeSubtask(task);
        calculateEpicStatus(epicId);
        historyManager.remove(id);
        freeIds.offer(id);
    }


    void calculateEpicStatus(int epicId) {
        Epic epic = epicTasks.get(epicId);
        List<Subtask> subtasks = getEpicSubtasks(epicId);
        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean allSubtasksNew = subtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);
        boolean allSubtasksDone = subtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);
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