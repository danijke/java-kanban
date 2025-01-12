package service;

import exception.ManagerSaveException;

import java.io.IOException;
import java.nio.file.*;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static FileBackedTaskManager getFileManager(Path path) {
        FileBackedTaskManager taskManager = null;
        try {
            if (Files.notExists(path) || Files.size(path) == 0) {
                taskManager = new FileBackedTaskManager(getDefaultHistory(), path);
            } else {
                taskManager = FileBackedTaskManager.loadFromFile(path);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: не удалось прочитать или создать файл");
        } catch (ManagerSaveException e) {
            System.err.println(e.getMessage());
        }
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
