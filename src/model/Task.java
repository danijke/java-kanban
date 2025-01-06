package model;

import service.TaskManager;

import java.io.IOException;
import java.time.*;
import java.util.Comparator;

public class Task {
    private final String title;
    private final String description;
    protected Duration duration;
    protected LocalDateTime startTime;
    private TaskStatus status;
    private int id;

    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    protected Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    protected Task(int id, String title, TaskStatus status, String description, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public static Comparator<Task> getComparator() {
        return Comparator.comparing(Task::getStartTime);
    }

    public static void fromString(String s, TaskManager fileManager) throws IOException {
        String[] data = s.split(",");
        switch (data[1]) {
            case "Task" -> {
                Task task = new Task(Integer.parseInt(data[0]), data[2], TaskStatus.toStatus(data[3]), data[4], LocalDateTime.parse(data[6]), Duration.ofMinutes(Integer.parseInt(data[7])));
                fileManager.addTask(task);
            }
            case "Epic" -> {
                Epic epic = new Epic(Integer.parseInt(data[0]), data[2], TaskStatus.toStatus(data[3]), data[4], LocalDateTime.parse(data[6]), Duration.ofMinutes(Integer.parseInt(data[7])));
                fileManager.addEpic(epic);
            }
            case "Subtask" -> {
                Subtask subtask = new Subtask(Integer.parseInt(data[0]), data[2], TaskStatus.toStatus(data[3]), data[4], Integer.parseInt(data[5]), LocalDateTime.parse(data[6]), Duration.ofMinutes(Integer.parseInt(data[7])));
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
        return String.format("%s,%s,%s,%s,%s,%d,%s,%d;%n", this.id, this.getClass().getSimpleName(), this.title, this.status, this.description, this.getEpicId(), this.startTime.toString(), this.duration.toMinutes());
    }
}

//todo добавить в методы добавления, удаления, изменения запись в tresets -
//todo доработать обновление времени и продолжительности эпика относительно подзадач-
//todo доработать опцию сохранения состояния в файл -
//todo добавить в тесты проверку новых полей
//todo поменять циклы на stream api ~
//todo
//todo
//todo
//todo
//todo
//todo
//todo
//todo
//todo
