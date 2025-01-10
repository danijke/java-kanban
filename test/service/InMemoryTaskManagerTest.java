package service;

import model.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.*;
import java.util.List;

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

    private Epic createEpic() {
        Epic epic = new Epic("Epic 1", "Description 1", LocalDateTime.now(), Duration.ZERO);
        inMemoryTaskManager.addEpic(epic);
        return epic;
    }

    private Task createTask() {
        Task task = new Task("Task 1", "Description 1", LocalDateTime.now(), Duration.ZERO);
        inMemoryTaskManager.addTask(task);
        return task;
    }

    private Subtask createSubtask(int epicId) {
        Subtask subtask = new Subtask("Subtask 1", "Description 1", epicId, LocalDateTime.now(), Duration.ZERO);
        inMemoryTaskManager.addSubtask(subtask);
        return subtask;
    }

    @Test
    @DisplayName("должен вычислять статус эпика по подзадачам")
    void shouldCalculateEpicStatus() {
        Epic epic = createEpic();

        Subtask subtask1 = createSubtask(epic.getId());
        Subtask subtask2 = createSubtask(epic.getId());

        inMemoryTaskManager.calculateEpicStatus(epic.getId());
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен быть NEW");

        subtask1.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.calculateEpicStatus(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");

        subtask2.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.calculateEpicStatus(epic.getId());
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE");
    }


    @Test
    @DisplayName("должен возвращать список отсортированных задач")
    void shouldGetSortedEntities() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());

        assertEquals(List.of(task, epic, subtask), inMemoryTaskManager.getSortedTasksById(), "Списки должны быть отсортированы по id");
    }

    @Test
    @DisplayName("должен добавлять задачи, эпики и подзадачи")
    void shouldAddEntities() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());

        assertAll(
                () -> assertEquals(task, inMemoryTaskManager.getTask(task.getId()), "Задача должна быть добавлена"),
                () -> assertEquals(epic, inMemoryTaskManager.getEpic(epic.getId()), "Эпик должен быть добавлен"),
                () -> assertEquals(subtask, inMemoryTaskManager.getSubtask(subtask.getId()), "Подзадача должна быть добавлена"));
    }

    @Test
    @DisplayName("должен обновлять задачи, эпики и подзадачи")
    void shouldUpdateEntities() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());

        Duration duration = Duration.ofMinutes(60);

        Task newTask = new Task("Updated Task", "Updated Description", task.getStartTime().plus(duration), duration);
        newTask.setId(task.getId());
        inMemoryTaskManager.updateTask(newTask);

        Epic newEpic = new Epic("Updated Epic", "Updated Description", epic.getStartTime().plus(duration), duration);
        newEpic.setId(epic.getId());
        inMemoryTaskManager.updateEpic(newEpic);

        Subtask newSubtask = new Subtask("Updated Subtask", "Updated Description", epic.getId(), subtask.getStartTime().plus(duration), duration);
        newSubtask.setId(subtask.getId());
        inMemoryTaskManager.updateSubtask(newSubtask);

        assertAll(
                () -> assertEquals(newTask, inMemoryTaskManager.getTask(task.getId()), "Задача должна быть обновлена"),
                () -> assertEquals(newEpic, inMemoryTaskManager.getEpic(epic.getId()), "Эпик должен быть обновлен"),
                () -> assertEquals(newSubtask, inMemoryTaskManager.getSubtask(subtask.getId()), "Подзадача должна быть обновлена"));
    }

    @Test
    @DisplayName("должен очищать все списки")
    void shouldClearAllLists() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());

        assertAll(() -> assertEquals(List.of(task, epic, subtask), inMemoryTaskManager.getSortedTasksById(), "Список задач должен совпадать"), () -> assertEquals(List.of(task), inMemoryTaskManager.getTasks(), "Список задач должен совпадать"), () -> assertEquals(List.of(epic), inMemoryTaskManager.getEpics(), "Список задач должен совпадать"), () -> assertEquals(List.of(subtask), inMemoryTaskManager.getSubtasks(), "Список задач должен совпадать"), () -> assertEquals(List.of(task, epic, subtask), inMemoryTaskManager.getPrioritizedTasks(), "Список отсортированных по времени задач должен совпадать"));

        inMemoryTaskManager.clearTasks();
        inMemoryTaskManager.clearEpics();
        inMemoryTaskManager.clearSubtasks();

        assertAll(() -> assertTrue(inMemoryTaskManager.getSortedTasksById().isEmpty(), "Список отсортированных задач должен быть пуст"), () -> assertTrue(inMemoryTaskManager.getTasks().isEmpty(), "Список задач должен быть пуст"), () -> assertTrue(inMemoryTaskManager.getEpics().isEmpty(), "Список эпиков должен быть пуст"), () -> assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Список подзадач должен быть пуст"), () -> assertTrue(inMemoryTaskManager.getPrioritizedTasks().isEmpty(), "Список отсортированных по времени задач должен быть пуст"));
    }

    @Test
    @DisplayName("должен удалять задачи, эпики и подзадачи")
    void shouldRemoveEntities() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());

        inMemoryTaskManager.removeTask(task.getId());
        inMemoryTaskManager.removeEpic(epic.getId());
        inMemoryTaskManager.removeSubtask(subtask.getId());

        assertAll(() -> assertNull(inMemoryTaskManager.getTask(task.getId()), "Задача должна быть удалена"), () -> assertNull(inMemoryTaskManager.getEpic(epic.getId()), "Эпик должен быть удален"), () -> assertNull(inMemoryTaskManager.getSubtask(subtask.getId()), "Подзадача должна быть удалена"));
    }

    @Test
    @DisplayName("должен проверить пересечение задач")
    void shouldCheckTaskCollisions() {
        Task task = new Task("Task 1", "Description 1", LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 10)), Duration.ofMinutes(50));
        inMemoryTaskManager.addTask(task);
        Epic epic = new Epic("Epic 1", "Description 1");
        inMemoryTaskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId(), LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 00)), Duration.ofMinutes(50));
        inMemoryTaskManager.addSubtask(subtask);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        inMemoryTaskManager.addSubtask(subtask1);

        assertTrue(inMemoryTaskManager.sortedTasks.contains(task), "Задача должна находиться в списке");
        assertTrue(inMemoryTaskManager.sortedTasks.contains(subtask), "Задача должна находиться в списке");

        assertFalse(inMemoryTaskManager.sortedTasks.contains(epic), "Задача не должна находиться в списке");
        assertFalse(inMemoryTaskManager.sortedTasks.contains(subtask1), "Задача не должна находиться в списке");

    }

}