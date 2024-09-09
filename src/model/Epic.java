package model;

import service.TaskStatus;

public class Epic extends Task {
    public Epic(String title, String description) {
        super(title, description);
    }

    public void setStatus(TaskStatus status, boolean AllSubtasksDone) {
        if (AllSubtasksDone) {
            this.status = TaskStatus.DONE;
        } else {
            this.status = TaskStatus.IN_PROGRESS;
        }

    }
}
