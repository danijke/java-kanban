package model;

import service.TaskManager;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class Task {
    private int id;
    private final String title;
    private TaskStatus status;
    private final String description;
    protected LocalDateTime startTime;
    protected Duration duration;

    protected static final  DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(int id, String title, TaskStatus status, String description, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public static Comparator<Task> getComparator() {
        return Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                         .thenComparing(Task:: getId);
    }

    public static void fromString(String s, TaskManager fileManager) throws IOException {
        String[] data = s.split(",");
        switch (data[1]) {
            case "Task" -> {
                LocalDateTime startTime = data[6].equals("null") ? null : LocalDateTime.parse(data[6], DATE_TIME_FORMATTER);
                Duration duration = data[7].equals("null") ? null : Duration.ofMinutes(Integer.parseInt(data[7]));
                Task task = new Task(Integer.parseInt(data[0]), data[2], TaskStatus.toStatus(data[3]), data[4], startTime, duration);
                fileManager.addTask(task);
            }
            case "Epic" -> {
                LocalDateTime startTime = data[6].equals("null") ? null : LocalDateTime.parse(data[6], DATE_TIME_FORMATTER);
                Duration duration = data[7].equals("null") ? null : Duration.ofMinutes(Integer.parseInt(data[7]));
                Epic epic = new Epic(Integer.parseInt(data[0]), data[2], TaskStatus.toStatus(data[3]), data[4], startTime, duration);
                fileManager.addEpic(epic);
            }
            case "Subtask" -> {
                LocalDateTime startTime = data[6].equals("null") ? null : LocalDateTime.parse(data[6], DATE_TIME_FORMATTER);
                Duration duration = data[7].equals("null") ? null : Duration.ofMinutes(Integer.parseInt(data[7]));
                Subtask subtask = new Subtask(Integer.parseInt(data[0]), data[2], TaskStatus.toStatus(data[3]), data[4], Integer.parseInt(data[5]), startTime, duration);
                fileManager.addSubtask(subtask);
            }
            default -> throw new IOException("не удалось прочитать файл ");
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return this.description;
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
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s%n", this.id, this.getClass().getSimpleName(), this.title, this.status.toString(), this.description, this.getEpicId(), (this.startTime != null ? this.startTime.format(DATE_TIME_FORMATTER) : "null"), (this.duration != null ? this.duration.toMinutes() : "null") // Проверка на null для duration
        );
    }

    public Duration getDuration() {
        return this.duration;
    }
}

