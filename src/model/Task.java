package model;
import service.*;
import java.util.Objects;

public abstract class Task {
    private static int counter = 0;
    private final String title;
    private final String description;
    private final int id;
    protected TaskStatus status;


    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.id = ++counter;
        this.status = TaskStatus.NEW;
    }

    public int getId() {
        return id;
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
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status);
    }
    @Override
    public String toString() {
        return getClass().getSimpleName()+ "{" +
                "Имя ='" + title + '\'' +
                ",Описание ='" + description + '\'' +
                ",id ='" + id + '\'' +
                ",статус ='" + status + '\'' + '}';

    }

}
