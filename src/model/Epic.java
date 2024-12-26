package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Epic extends Task {
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(int id, String title, TaskStatus status, String description) {
        super(id, title, status, description);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }


    public void setDuration(Duration duration) {
        this.duration = this.duration.plus(duration);
    }


}