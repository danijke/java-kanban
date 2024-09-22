package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    List<Subtask> getEpicSubtasks(Epic epic);

    List<Task> getTasks();

    List<Task> getEpics();

    List<Task> getSubtasks();

    void addTask(Task task);

    void addEpic(Epic task);

    void addSubtask(Subtask task);

    void updateTask(Task task);

    void updateEpic(Epic task);

    void updateSubtask(Subtask task);

    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);
}
