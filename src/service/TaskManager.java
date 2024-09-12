package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private int counter = 0;

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epicTasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        if (subtasks.isEmpty()) {
            return null;
        } else {
            ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
            for (Subtask subtask : subtasks.values()) {
                if (epic.getId() == subtask.getEpicId()) {
                    subtasksByEpic.add(subtask);
                }
            }
            return subtasksByEpic.isEmpty() ? null : subtasksByEpic;
        }
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Task> getEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<Task> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void addTask(Task task) {
        int id = generateId(task);
        tasks.put(id, task);
    }

    public void addEpic(Epic task) {
        int id = generateId(task);
        epicTasks.put(id, task);
    }

    public void addSubtask(Subtask task) {
        int id = generateId(task);
        subtasks.put(id, task);
        if (task.getStatus() != TaskStatus.NEW) {
            setEpicStatus(task.getEpicId());
        }
    }

    public void updateTask(Task task) {
        int id = task.getId();
        tasks.remove(id);
        tasks.put(id, task);
    }

    public void updateEpic(Epic task) {
        int id = task.getId();
        epicTasks.remove(id);
        epicTasks.put(id, task);
    }

    public void updateSubtask(Subtask task) {
        int id = task.getId();
        subtasks.remove(id);
        subtasks.put(id, task);
        if (task.getStatus() != TaskStatus.NEW) {
            setEpicStatus(task.getEpicId());
        }
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epicTasks.clear();
        subtasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epicTasks.values()) {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id) {
                subtasks.remove(subtask.getId());
            }
        }
        epicTasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        setEpicStatus(epicId);
    }

    private int generateId(Task task) {
        int id = ++counter;
        task.setId(id);
        return id;
    }

    private void setEpicStatus(int epicId) {
        boolean AllSubtasksDone = true;
        Epic epic = epicTasks.get(epicId);
        if (getSubtasksByEpic(epic) == null) {
            epic.setStatus(TaskStatus.NEW);
            return;
        } else {
            for (Subtask subtask : getSubtasksByEpic(epic)) {
                if (subtask.getStatus() != TaskStatus.DONE) {
                    AllSubtasksDone = false;
                    break;
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