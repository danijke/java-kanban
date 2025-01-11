package service;

import model.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер файлов")
class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    Path path;

    @BeforeEach
    void initTaskManager() {
        try {
            path = Files.createTempFile("tmp", "csv");
            taskManager = Managers.getFileManager(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Должен добавлять и записывать в историю")
    void shouldSaveTask() {

    }

    @Test
    @DisplayName("Должен сохранять пустой файл")
    void shouldSaveEmptyToFile() {
        assertDoesNotThrow(taskManager::save, "не должно выбрасываться исключение при сохранении файла");
    }

    @Test
    @DisplayName("Должен сохранить и загрузить задачи")
    void shouldSaveAndLoadTask() {
        Task task = assertDoesNotThrow(this::createTask, "не должно выбрасываться исключение при сохранении файла");
        Epic epic = assertDoesNotThrow(this::createEpic, "не должно выбрасываться исключение при сохранении файла");
        Subtask subtask = assertDoesNotThrow(() -> createSubtask(epic.getId()), "не должно выбрасываться исключение при сохранении файла");
        assertEquals(List.of(task, epic, subtask),
                assertDoesNotThrow(
                        () -> FileBackedTaskManager.loadFromFile(path).getSortedTasksById(),
                        "не должно выбрасываться исключение при сохранении файла"),
                "списки должны совпадать");
    }

    @Test
    @DisplayName("Должен загружать пустой файл")
    void shouldLoadEmptyFile() {
        assertDoesNotThrow(() -> {
            Path empty = Files.createTempFile("empty", "csv");
            assertEquals(0, Files.size(empty), "Файл должен быть пустой");
            FileBackedTaskManager.loadFromFile(empty);
            Files.delete(empty);
        });
    }

}