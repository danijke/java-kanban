package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int counter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();

    public int generateId(Task task) {
        int id = ++counter;
        task.setId(id);
        return id;
    }

    public void addTask(Task task) {
        int id = generateId(task);
        tasks.put(id, task);
    }

    public void addTask(Epic task) {
        int id = generateId(task);
        epicTasks.put(id, task);
    }

    public void addTask(Subtask task) {
        int id = generateId(task);
        subtasks.put(id, task);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task getEpicById(int id) {
        return epicTasks.get(id);
    }

    public Task getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epicTasks.values());
        allTasks.addAll(subtasks.values());

        return allTasks;
    }

    public void updateTask(Task task) {
        int id = task.getId();
        tasks.remove(id);
        tasks.put(id, task);
    }

    public void updateTask(Epic task) {
        int id = task.getId();
        epicTasks.remove(id);
        epicTasks.put(id, task);
    }

    public void updateTask(Subtask task) {
        int id = task.getId();
        subtasks.remove(id);
        subtasks.put(id, task);
        if (task.getStatus() != TaskStatus.NEW) {
            Epic epic = epicTasks.get(task.getEpicId());
            boolean AllSubtasksDone = true;
            for (Subtask subtask : subtasks.values()) {
                if (task.getEpicId() == subtask.getEpicId()) {
                    if (subtask.getStatus() != TaskStatus.DONE) {
                        AllSubtasksDone = false;
                    }
                }
            }
            if (AllSubtasksDone) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epicTasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        epicTasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        subtasks.remove(id);
    }

    public ArrayList<Subtask> getSubtaskByEpic(Epic task) {
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        int epicId = task.getId();
        for (Subtask subtask : subtasks.values()) {
            if (epicId == subtask.getEpicId()) {
                subtasksByEpic.add(subtask);
            }
        }
        return subtasksByEpic;
    }
}

