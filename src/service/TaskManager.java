package service;

import model.*;

import java.util.List;

public interface TaskManager {
    List<Task> getSortedTasksById();
    List<Task> getPrioritizedTasks();
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    List<Subtask> getEpicSubtasks(int id);

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
