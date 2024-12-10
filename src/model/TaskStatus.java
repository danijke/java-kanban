package model;

public enum TaskStatus {
    NEW,
    IN_PROGRESS,
    DONE;

    public static TaskStatus toStatus(String str) {
        switch (str) {
            case "NEW" -> {
                return TaskStatus.NEW;
            }
            case "IN_PROGRESS" -> {
                return TaskStatus.IN_PROGRESS;
            }
            case "DONE" -> {
                return TaskStatus.DONE;
            }
        }
        return null;
    }
}