package service;

import exception.*;
import model.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract public class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected Epic createEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        return epic;
    }

    protected Task createTask() {
        Task task = new Task("Task 1", "Description 1", LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 10)), Duration.ofMinutes(50));
        taskManager.addTask(task);
        return task;
    }

    protected Subtask createSubtask(int epicId) {
        Subtask subtask = new Subtask("Subtask 1", "Description 1", epicId, LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)), Duration.ofMinutes(50));
        taskManager.addSubtask(subtask);
        return subtask;
    }
    protected Subtask createAnSubtask(int epicId) {
        Subtask subtask = new Subtask("Subtask 2", "Description 2", epicId, LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 50)), Duration.ofMinutes(50));
        taskManager.addSubtask(subtask);
        return subtask;
    }

    @Test
    @DisplayName("должен вычислять статус эпика по подзадачам")
    void shouldCalculateEpicStatus() {
        Epic epic = createEpic();

        Subtask subtask1 = createSubtask(epic.getId());
        Subtask subtask2 = createAnSubtask(epic.getId());

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен быть NEW");

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");

        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE");
    }


    @Test
    @DisplayName("должен возвращать список отсортированных задач")
    void shouldGetSortedEntities() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());

        assertEquals(List.of(task, epic, subtask), taskManager.getSortedTasksById(), "Списки должны быть отсортированы по id");
    }

    @Test
    @DisplayName("должен добавлять задачи, эпики и подзадачи")
    void shouldAddEntities() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());

        assertAll(
                () -> assertEquals(task, taskManager.getTask(task.getId()), "Задача должна быть добавлена"),
                () -> assertEquals(epic, taskManager.getEpic(epic.getId()), "Эпик должен быть добавлен"),
                () -> assertEquals(subtask, taskManager.getSubtask(subtask.getId()), "Подзадача должна быть добавлена"));
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
        taskManager.updateTask(newTask);

        Epic newEpic = new Epic("Updated Epic", "Updated Description", epic.getStartTime().plus(duration), duration);
        newEpic.setId(epic.getId());
        taskManager.updateEpic(newEpic);

        Subtask newSubtask = new Subtask("Updated Subtask", "Updated Description", epic.getId(), subtask.getStartTime().plus(duration), duration);
        newSubtask.setId(subtask.getId());
        taskManager.updateSubtask(newSubtask);

        assertAll(
                () -> assertEquals(newTask, taskManager.getTask(task.getId()), "Задача должна быть обновлена"),
                () -> assertEquals(newEpic, taskManager.getEpic(epic.getId()), "Эпик должен быть обновлен"),
                () -> assertEquals(newSubtask, taskManager.getSubtask(subtask.getId()), "Подзадача должна быть обновлена"));
    }

    @Test
    @DisplayName("должен очищать все списки")
    void shouldClearAllLists() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());

        assertAll(() -> assertEquals(List.of(task, epic,subtask), taskManager.getSortedTasksById(), "Список задач должен совпадать"), () -> assertEquals(List.of(task), taskManager.getTasks(), "Список задач должен совпадать"), () -> assertEquals(List.of(epic), taskManager.getEpics(), "Список задач должен совпадать"), () -> assertEquals(List.of(subtask), taskManager.getSubtasks(), "Список задач должен совпадать"), () -> assertEquals(List.of(task, subtask), taskManager.getPrioritizedTasks(), "Список отсортированных по времени задач должен совпадать"));

        taskManager.clearTasks();
        taskManager.clearEpics();
        taskManager.clearSubtasks();

        assertAll(() -> assertTrue(taskManager.getSortedTasksById().isEmpty(), "Список отсортированных задач должен быть пуст"), () -> assertTrue(taskManager.getTasks().isEmpty(), "Список задач должен быть пуст"), () -> assertTrue(taskManager.getEpics().isEmpty(), "Список эпиков должен быть пуст"), () -> assertTrue(taskManager.getSubtasks().isEmpty(), "Список подзадач должен быть пуст"), () -> assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Список отсортированных по времени задач должен быть пуст"));
    }

    @Test
    @DisplayName("должен удалять задачи, эпики и подзадачи")
    void shouldRemoveEntities() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());

        taskManager.removeTask(task.getId());
        taskManager.removeEpic(epic.getId());
        taskManager.removeSubtask(subtask.getId());
        assertThrows(NotFoundException.class, () -> taskManager.getTask(task.getId()), "должно выбрасываться исключение при получении задачи");
        assertThrows(NotFoundException.class,() ->taskManager.getEpic(epic.getId()), "должно выбрасываться исключение при получении эпика");
        assertThrows(NotFoundException.class,() ->taskManager.getSubtask(subtask.getId()), "должно выбрасываться исключение при получении подзадачи");
    }

    @Test
    @DisplayName("должен проверить пересечение задач")
    void shouldCheckTaskCollisions() {
        Task task = new Task("Task 1", "Description 1", LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 10)), Duration.ofMinutes(50));
        assertDoesNotThrow(() -> taskManager.addTask(task), "не должно выбрасываться исключение при добавлении задачи");

        Epic epic = new Epic("Epic 1", "Description 1");
        assertDoesNotThrow(() -> taskManager.addEpic(epic), "не должно выбрасываться исключение при добавлении задачи");

        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId(), LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)), Duration.ofMinutes(50));
        assertDoesNotThrow(() -> taskManager.addSubtask(subtask), "не должно выбрасываться исключение при добавлении задачи");

        Subtask subtask1 = new Subtask("Subtask 2", "Description 2", epic.getId(), LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)), Duration.ofMinutes(50));
        assertThrows(InteractedException.class,() -> taskManager.addSubtask(subtask1), "должно выбрасываться исключение при добавлении задачи");

        assertTrue(taskManager.getPrioritizedTasks().contains(task), "Задача должна находиться в списке");
        assertTrue(taskManager.getPrioritizedTasks().contains(subtask), "Задача должна находиться в списке");
        assertFalse(taskManager.getPrioritizedTasks().contains(epic), "Задача не должна находиться в списке");
        assertFalse(taskManager.getPrioritizedTasks().contains(subtask1), "Задача не должна находиться в списке");
    }
}