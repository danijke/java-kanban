package model;

import service.TaskStatus;

public class Task {
    private final String title;
    private final String description;
    private TaskStatus status;
    private int id;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "Имя ='" + this.title + '\''
                                        + ",Описание ='" + this.description + '\''
                                        + ",id ='" + this.id + '\''
                                        + ",статус ='" + this.status + '\'' + '}';
    }
}