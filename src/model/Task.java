package model;

import service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private final String title;
    private final String description;
    private TaskStatus status;
    private int id;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String title, String description, long duration) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.now();
    }

    protected Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    protected Task(int id, String title, TaskStatus status, String description) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.description = description;
    }

    public static void fromString(String s, TaskManager fileManager) throws IOException {
        String[] data = s.split(",");
        switch (data[1]) {
            case "Task" -> {
                Task task = new Task(Integer.parseInt(data[0]), data[2], TaskStatus.toStatus(data[3]), data[4]);
                fileManager.addTask(task);
            }
            case "Epic" -> {
                Epic epic = new Epic(Integer.parseInt(data[0]), data[2], TaskStatus.toStatus(data[3]), data[4]);
                fileManager.addEpic(epic);
            }
            case "Subtask" -> {
                Subtask subtask = new Subtask(Integer.parseInt(data[0]), data[2], TaskStatus.toStatus(data[3]), data[4], Integer.parseInt(data[5]));
                fileManager.addSubtask(subtask);
            }
            default -> throw new IOException("не удалось прочитать файл");
        }
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

    public Integer getEpicId() {
        return null;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
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
        return String.format("%s,%s,%s,%s,%s,%s;%n", this.id, this.getClass().getSimpleName(), this.title, this.status, this.description, this.getEpicId());
    }
}