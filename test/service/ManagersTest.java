package service;

import model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Менеджер объектов")
class ManagersTest {
    TaskManager taskManager;
    HistoryManager historyManager;
    TaskManager fileManager;
    Task task;

    @BeforeEach
    void init() {
        task = new Task("taskTitle", "taskD");
    }

    @Test
    @DisplayName("должен возвращать проинициализированный объект InMemoryTaskManager")
    void shouldGetDefault() {
        taskManager = Managers.getDefault();
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTask(task.getId()));

    }

    @Test
    @DisplayName("должен возвращать проинициализированный объект InMemoryHistoryManager")
    void getDefaultHistory() {
        historyManager = Managers.getDefaultHistory();
        historyManager.add(task);
        assertEquals(List.of(task), historyManager.getHistory());
    }

    @Test
    @DisplayName("должен возвращать проинициализированный объект FileBackedTaskManager")
    void getFileManager() {
        Path path;
        try {
            path = Files.createTempFile("tmp", ".csv");
            fileManager = Managers.getFileManager(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileManager.addTask(task);
        assertEquals(task, fileManager.getTask(task.getId()));
    }
}