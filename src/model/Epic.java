package model;

import java.time.*;
import java.util.TreeSet;

public class Epic extends Task {
    TreeSet<Subtask> subtasks = new TreeSet<>(Task.getComparator());
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, LocalDateTime startTime, Duration duration) {
        super(title, description, startTime, duration);
    }

    public Epic(int id, String title, TaskStatus status, String description, LocalDateTime startTime, Duration duration) {
        super(id, title, status, description, startTime, duration);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void addSubtask(Subtask task) {
        subtasks.add(task);
        calculateEpicTime();
    }

    public void updateSubtask(Subtask oldTask, Subtask newTask) {
        subtasks.remove(oldTask);
        subtasks.add(newTask);
        calculateEpicTime();
    }

    public void removeSubtask(Subtask task) {
        subtasks.remove(task);
        calculateEpicTime();
    }

    public void clearSubtask() {
        subtasks.clear();
        calculateEpicTime();
    }

    private void calculateEpicTime() {
        if (!subtasks.isEmpty()) {
            this.startTime = subtasks.first().getStartTime();
            this.endTime = subtasks.last().getEndTime();
            this.duration = subtasks.stream().map(Subtask::getDuration).reduce(Duration.ZERO, Duration::plus);
        } else {
            this.startTime = null;
            this.endTime = null;
            this.duration = Duration.ZERO;
        }
    }
}