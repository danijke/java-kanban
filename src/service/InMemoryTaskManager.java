package service;

import exception.*;
import model.*;

import java.util.*;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    TreeSet<Task> sortedTasks = new TreeSet<>(Task.getComparator());
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    Queue<Integer> freeIds = new LinkedList<>();
    HistoryManager historyManager;
    private int counter = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getHistory() {
        return this.historyManager.getHistory();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        } else {
            throw new NotFoundException("задача c id: " + id + " не найдена.");
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic task = epics.get(id);
        if (task != null) {
            historyManager.add(task);
        } else {
            throw new NotFoundException("эпик c id: " + id + " не найден.");
        }
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask task = subtasks.get(id);
        if (task != null) {
            historyManager.add(task);
        } else {
            throw new NotFoundException("подзадача c id: " + id + " не найдена.");
        }
        return task;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().toList();
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        return subtasks.values().stream().filter(subtask -> id == subtask.getEpicId()).toList();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getSortedTasksById() {
        return Stream.of(tasks.values(), epics.values(), subtasks.values())
                .flatMap(Collection::stream).map(task -> (Task) task)
                .sorted(Comparator.comparingInt(Task::getId))
                .toList();
    }

    @Override
    public void addTask(Task task) throws InteractedException {
        generateId(task);
        if (isTimeValid(task)) {
            if (isCrossing(task)) {
                throw new InteractedException();
            }
            sortedTasks.add(task);
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        generateId(epic);
        if (isTimeValid(epic)) {
            if (isCrossing(epic)) {
                throw new InteractedException();
            }
            sortedTasks.add(epic);
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        generateId(subtask);
        int epicId = subtask.getEpicId();
        if (isTimeValid(subtask)) {
            if (isCrossing(subtask)) {
                throw new InteractedException();
            }
            sortedTasks.add(subtask);
            Optional.ofNullable(epics.get(epicId))
                    .orElseThrow(() -> new NotFoundException("эпик с id: " + epicId + " не найден"))
                    .addSubtask(subtask);
        }
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStatus() != TaskStatus.NEW) {
            calculateEpicStatus(epicId);
        }
    }

    @Override
    public void updateTask(Task task) {
        Task old = tasks.put(task.getId(), task);
        if (sortedTasks.remove(old) && isTimeValid(task) && !isCrossing(task)) sortedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic task) {
        Epic old = epics.put(task.getId(), task);
        if (sortedTasks.remove(old) && isTimeValid(task) && !isCrossing(task)) sortedTasks.add(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        Subtask old = subtasks.put(id, subtask);
        int epicId = subtask.getEpicId();
        if (sortedTasks.remove(old) && isTimeValid(subtask) && !isCrossing(subtask)) {
            sortedTasks.add(subtask);
            Optional.ofNullable(epics.get(epicId))
                    .orElseThrow(() -> new NotFoundException("эпик с id: " + epicId + " не найден"))
                    .updateSubtask(old,subtask);
        }
        if (subtask.getStatus() != TaskStatus.NEW) calculateEpicStatus(epicId);
    }

    @Override
    public void removeTask(int id) {
        Optional.ofNullable(tasks.remove(id)).ifPresent(task -> sortedTasks.remove(task));
        removeUtils(id);
    }

    @Override
    public void removeEpic(int id) {
        getEpicSubtasks(id).forEach(subtask -> {
            int subtaskId = subtask.getId();
            sortedTasks.remove(subtask);
            subtasks.remove(subtaskId);
            removeUtils(subtaskId);
        });
        Optional.ofNullable(epics.remove(id)).ifPresent(epic -> sortedTasks.remove(epic));
        removeUtils(id);
    }

    @Override
    public void removeSubtask(int id) {
        Optional.ofNullable(subtasks.remove(id)).ifPresent(task -> {
            sortedTasks.remove(task);
            int epicId = task.getEpicId();
            epics.get(epicId).removeSubtask(task);
            calculateEpicStatus(epicId);
            removeUtils(id);
        });
    }

    @Override
    public void clearTasks() {
        tasks.forEach((id, task) -> {
            sortedTasks.remove(task);
            removeUtils(id);
        });
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        Stream.concat(epics.values().stream(), subtasks.values().stream()).forEach(task -> {
            if (task.getStartTime() != null) {
                sortedTasks.remove(task);
            }
            removeUtils(task.getId());
        });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        subtasks.forEach((id, subtask) -> {
            sortedTasks.remove(subtask);
            removeUtils(id);
        });
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.setStatus(TaskStatus.NEW);
            epic.clearSubtask();
        });
    }

    void calculateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> subtasks = getEpicSubtasks(epicId);
        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean allSubtasksNew = subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);
        boolean allSubtasksDone = subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);
        if (allSubtasksNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allSubtasksDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    void removeUtils(int id) {
        historyManager.remove(id);
        freeIds.offer(id);
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

    Boolean isTimeValid(Task task) {
        return Optional.ofNullable(task.getStartTime())
                .isPresent();
    }

    Boolean isCrossing(Task task) {
        return Optional.ofNullable(sortedTasks.lower(task))
                .map(before -> before.getEndTime() != null && before.getEndTime().isAfter(task.getStartTime()))
                .orElse(false)
                ||
               Optional.ofNullable(sortedTasks.higher(task))
                .map(after -> task.getEndTime() != null && task.getEndTime().isAfter(after.getStartTime()))
                .orElse(false);
    }
}