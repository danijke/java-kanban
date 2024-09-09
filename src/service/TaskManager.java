package service;

import model.*;

import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, DefaultTask> defaultTasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();


    public void addTask(DefaultTask task) {
        tasks.put(task.getId(), task);
        defaultTasks.put(task.getId(), task);
    }

    public void addTask(Epic task) {
        tasks.put(task.getId(), task);
        epicTasks.put(task.getId(), task);
    }

    public void addTask(Subtask task) {
        tasks.put(task.getId(), task);
        subtasks.put(task.getId(), task);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public void updateTask(DefaultTask task) {
        int id = task.getId();
        tasks.remove(id);
        tasks.put(id, task);
        defaultTasks.remove(id);
        defaultTasks.put(id, task);
    }

    public void updateTask(Epic task) {
        int id = task.getId();
        tasks.remove(id);
        tasks.put(id, task);
        epicTasks.remove(id);
        epicTasks.put(id, task);
    }

    public void updateTask(Subtask task) {
        int id = task.getId();
        tasks.remove(id);
        tasks.put(id, task);
        subtasks.remove(id);
        subtasks.put(id, task);
        Epic epic = epicTasks.get(task.getEpicId());
        if (task.getStatus() != TaskStatus.NEW) {
            boolean AllSubtasksDone = true;
            for (Subtask subtask : subtasks.values()) {
                if (task.getEpicId() == subtask.getEpicId()) {
                    if (subtask.getStatus() != TaskStatus.DONE) {
                        AllSubtasksDone = false;
                    }
                }
            }
            epic.setStatus(task.getStatus(), AllSubtasksDone);
        }
    }

    public void removeAllTasks() {
        tasks.clear();
        defaultTasks.clear();
        subtasks.clear();
        epicTasks.clear();
    }

    public void removeById(int id) {
        tasks.remove(id);
        if (defaultTasks.containsKey(id)) {
            defaultTasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            subtasks.remove(id);
        } else {
            epicTasks.remove(id);
        }
    }

    public HashMap<Integer, Subtask> getSubtaskByEpic(Epic task) {
        HashMap<Integer, Subtask> subtasksByEpic = new HashMap<>();
        int epicId = task.getId();
        for (Subtask subtask : subtasks.values()) {
            if (epicId == subtask.getEpicId()) {
                subtasksByEpic.put(subtask.getId(), subtask);
            }
        }
        return subtasksByEpic;
    }
}
