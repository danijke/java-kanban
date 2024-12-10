package service;

import model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер файлов")
class FileBackedTaskManagerTest {
    FileBackedTaskManager fileManager;
    Path path;
    Task task;

    @BeforeEach
    void init() {
        try {
            path = Files.createTempFile("tmp", "csv");
            fileManager = Managers.getFileManager(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        task = new Task("taskTitle", "taskD");
    }

    @Test
    @DisplayName("Должен добавлять, получать задачу и записывать в историю")
    void shouldAddAndGetTaskAndSaveToHistory() {
        fileManager.addTask(task);
        assertEquals(task, fileManager.getTask(task.getId()));
        assertEquals(List.of(task), fileManager.getHistory());
    }

    @Test
    @DisplayName("Должен сохранять пустой файл")
    void shouldSaveEmptyToFile() {
        fileManager.addTask(task);
        fileManager.removeTask(task.getId());
    }

    @Test
    @DisplayName("Должен сохранить и загрузить задачи")
    void shouldSaveAndLoadTask() {
        fileManager.addTask(task);
        FileBackedTaskManager fm = FileBackedTaskManager.loadFromFile(path);
        assertEquals(fileManager.getTask(task.getId()), fm.getTask(task.getId()));
    }

    @Test
    @DisplayName("Должен загружать пустой файл")
    void shouldLoadEmptyFile() {
        try {
            Path empty = Files.createTempFile("empty", "csv");
            FileBackedTaskManager.loadFromFile(empty);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}