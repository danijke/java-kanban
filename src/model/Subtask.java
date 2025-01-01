package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, TaskStatus status, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(id, title, status, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(title,description, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public Duration getDuration() {
        return duration;
    }
}
