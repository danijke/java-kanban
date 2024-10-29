package model;

public class Epic extends Task {
    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(int id, String title, TaskStatus status, String description) {
        super(id, title, status, description);
    }
}