package model;

import java.time.Duration;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, TaskStatus status, String description, int epicId) {
        super(id, title, status, description);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId, long duration) {
        super(title,description, duration);
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
