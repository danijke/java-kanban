package model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        setEpicId(epicId);
    }

    public Subtask(int id, String title, TaskStatus status, String description, int epicId) {
        super(id, title, status, description);
        this.epicId = epicId;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
