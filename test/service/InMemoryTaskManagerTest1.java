package service;

import model.*;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.List;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryTaskManager")
class InMemoryTaskManagerTest {

    @Mock
    private InMemoryHistoryManager inMemoryHistoryManager;
    private InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        inMemoryTaskManager = new InMemoryTaskManager(inMemoryHistoryManager);
    }

    private Epic createEpic(String title, String description, Duration duration) {
        Epic epic = new Epic(title, description);
        inMemoryTaskManager.addEpic(epic);
        return epic;
    }

    private Task createTask(String title, String description, Duration duration) {
        Task task = new Task(title, description);
        inMemoryTaskManager.addTask(task);
        return task;
    }

    private Subtask createSubtask(String title, String description, int epicId) {
        Subtask subtask = new Subtask(title, description, epicId);
        inMemoryTaskManager.addSubtask(subtask);
        return subtask;
    }

    @Test
    @DisplayName("должен вычислять статус эпика по подзадачам")
    void shouldCalculateEpicStatus() {
        Epic epic = createEpic("Epic 1", "Description 1");

        Subtask subtask1 = createSubtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = createSubtask("Subtask 2", "Description 2", epic.getId());

        // Проверка для статуса NEW
        inMemoryTaskManager.calculateEpicStatus(epic.getId());
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен быть NEW");

        // Проверка для статуса IN_PROGRESS
        subtask1.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.calculateEpicStatus(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");

        // Проверка для статуса DONE
        subtask2.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.calculateEpicStatus(epic.getId());
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE");
    }

    @Test
    @DisplayName("должен добавлять задачи, эпики и подзадачи")
    void shouldAddEntities() {
        Task task = createTask("Task 1", "Description 1");
        Epic epic = createEpic("Epic 1", "Description 1");
        Subtask subtask = createSubtask("Subtask 1", "Description 1", epic.getId());

        assertAll(
                () -> assertEquals(task, inMemoryTaskManager.getTask(task.getId()), "Задача должна быть добавлена"),
                () -> assertEquals(epic, inMemoryTaskManager.getEpic(epic.getId()), "Эпик должен быть добавлен"),
                () -> assertEquals(subtask, inMemoryTaskManager.getSubtask(subtask.getId()), "Подзадача должна быть добавлена")
        );
    }

    @Test
    @DisplayName("должен обновлять задачи, эпики и подзадачи")
    void shouldUpdateEntities() {
        Task task = createTask("Task 1", "Description 1");
        Epic epic = createEpic("Epic 1", "Description 1");
        Subtask subtask = createSubtask("Subtask 1", "Description 1", epic.getId());

        task.setTitle("Updated Task");
        task.setDescription("Updated Description");
        inMemoryTaskManager.updateTask(task);

        epic.setTitle("Updated Epic");
        epic.setDescription("Updated Description");
        inMemoryTaskManager.updateEpic(epic);

        subtask.setTitle("Updated Subtask");
        subtask.setDescription("Updated Description");
        inMemoryTaskManager.updateSubtask(subtask);

        assertAll(
                () -> assertEquals(task, inMemoryTaskManager.getTask(task.getId()), "Задача должна быть обновлена"),
                () -> assertEquals(epic, inMemoryTaskManager.getEpic(epic.getId()), "Эпик должен быть обновлен"),
                () -> assertEquals(subtask, inMemoryTaskManager.getSubtask(subtask.getId()), "Подзадача должна быть обновлена")
        );
    }

    @Test
    @DisplayName("должен очищать все списки")
    void shouldClearAllLists() {
        Task task = createTask("Task 1", "Description 1");
        Epic epic = createEpic("Epic 1", "Description 1");
        Subtask subtask = createSubtask("Subtask 1", "Description 1", epic.getId());

        inMemoryTaskManager.clearTasks();
        inMemoryTaskManager.clearEpics();
        inMemoryTaskManager.clearSubtasks();

        assertAll(
                () -> assertTrue(inMemoryTaskManager.getTasks().isEmpty(), "Список задач должен быть пуст"),
                () -> assertTrue(inMemoryTaskManager.getEpics().isEmpty(), "Список эпиков должен быть пуст"),
                () -> assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Список подзадач должен быть пуст")
        );
    }

    @Test
    @DisplayName("должен удалять задачи, эпики и подзадачи")
    void shouldRemoveEntities() {
        Task task = createTask("Task 1", "Description 1");
        Epic epic = createEpic("Epic 1", "Description 1");
        Subtask subtask = createSubtask("Subtask 1", "Description 1", epic.getId());

        inMemoryTaskManager.removeTask(task.getId());
        inMemoryTaskManager.removeEpic(epic.getId());
        inMemoryTaskManager.removeSubtask(subtask.getId());

        assertAll(
                () -> assertNull(inMemoryTaskManager.getTask(task.getId()), "Задача должна быть удалена"),
                () -> assertNull(inMemoryTaskManager.getEpic(epic.getId()), "Эпик должен быть удален"),
                () -> assertNull(inMemoryTaskManager.getSubtask(subtask.getId()), "Подзадача должна быть удалена")
        );
    }

}