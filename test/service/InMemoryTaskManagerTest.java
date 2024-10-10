package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryTaskManager")
class InMemoryTaskManagerTest {
    Task task;
    Epic epic;
    Subtask subtask;
    InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    void init() {
        EmptyHistoryManager historyManager = new EmptyHistoryManager();
        inMemoryTaskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    @DisplayName("должен вычислять статус эпика по подзадачам")
    void shouldCalculateEpicStatus() {
        epic = new Epic("epicTitle", "epicD");
        int epicId = inMemoryTaskManager.generateId(epic);
        inMemoryTaskManager.epicTasks.put(epicId, epic);

        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
        int subtaskId = inMemoryTaskManager.generateId(subtask);
        inMemoryTaskManager.subtasks.put(subtaskId, subtask);

        Subtask anotherSubtask = new Subtask("subtaskTitle2", "subtaskD2", epic.getId());
        int anotherSubtaskId = inMemoryTaskManager.generateId(anotherSubtask);
        inMemoryTaskManager.subtasks.put(anotherSubtaskId, anotherSubtask);

        inMemoryTaskManager.calculateEpicStatus(epicId);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "статусы должны совпадать");

        anotherSubtask.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.calculateEpicStatus(epicId);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "статусы должны совпадать");

        subtask.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.calculateEpicStatus(epicId);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "статусы должны совпадать");
    }

    @Test
    @DisplayName("должен добавлять задачу в ХешМап")
    void shouldAddTask() {
        task = new Task("taskTitle", "taskD");
        inMemoryTaskManager.addTask(task);
        Task savedTask = inMemoryTaskManager.tasks.get(task.getId());
        assertEquals(task, savedTask, "задачи должны совпадать");
    }

    @Test
    @DisplayName("должен добавлять Эпик в ХешМап")
    void shouldAddEpic() {
        epic = new Epic("epicTitle", "epicD");
        inMemoryTaskManager.addEpic(epic);
        Epic savedEpic = inMemoryTaskManager.epicTasks.get(epic.getId());

        assertEquals(epic, savedEpic, "эпики должны совпадать");
    }

    @Test
    void shouldAddSubtask() {
        epic = new Epic("epicTitle", "epicD");
        inMemoryTaskManager.addTask(epic);
        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
        inMemoryTaskManager.addSubtask(subtask);
        Subtask savedSubtask = inMemoryTaskManager.subtasks.get(subtask.getId());

        assertEquals(subtask, savedSubtask, "подзадачи должны совпадать");
    }

    @Test
    @DisplayName("должен получить верную задачу")
    void shouldGetTask() {
        task = new Task("taskTitle", "taskD");
        inMemoryTaskManager.addTask(task);
        Task savedTask = inMemoryTaskManager.getTask(task.getId());
        assertEquals(task, savedTask, "Задачи должны совпадать");
    }

    @Test
    @DisplayName("должен получить верный Эпик")
    void shouldGetEpic() {
        epic = new Epic("epicTitle", "epicD");
        inMemoryTaskManager.addEpic(epic);
        Epic savedEpic = inMemoryTaskManager.getEpic(epic.getId());
        assertEquals(epic, savedEpic, "Эпики должны совпадать");
    }

    @Test
    @DisplayName("должен получить верную подзадачу")
    void shouldGetSubtask() {
        epic = new Epic("epicTitle", "epicD");
        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
        inMemoryTaskManager.addSubtask(subtask);
        Subtask savedSubtask = inMemoryTaskManager.getSubtask(subtask.getId());

        assertEquals(subtask, savedSubtask, "Подзадачи должны совпадать");
    }

    @Test
    @DisplayName("должен получить подзадачи эпика")
    void shouldGetEpicSubtasks() {
        epic = new Epic("epicTitle", "epicD");
        inMemoryTaskManager.addEpic(epic);
        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
        inMemoryTaskManager.addSubtask(subtask);
        assertEquals(List.of(subtask), inMemoryTaskManager.getEpicSubtasks(epic), "Списки должны совпадать");
    }

    @Test
    @DisplayName("должен получить cписок задач")
    void shouldGetTasks() {
        task = new Task("taskTitle", "taskD");
        Task anotherTask = new Task("anotherTitle", "anotherTaskD");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(anotherTask);
        assertEquals(List.of(task, anotherTask), inMemoryTaskManager.getTasks(), "списки задач должны совпадать");
    }

    @Test
    @DisplayName("должен получить cписок эпиков")
    void shouldGetEpics() {
        epic = new Epic("epicTitle", "epicD");
        inMemoryTaskManager.addEpic(epic);
        assertEquals(List.of(epic), inMemoryTaskManager.getEpics(), "списки эпиков должны совпадать");
    }

    @Test
    @DisplayName("должен получить cписок подзадач")
    void shouldGetSubtasks() {
        epic = new Epic("epicTitle", "epicD");
        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
        inMemoryTaskManager.addSubtask(subtask);
        assertEquals(List.of(subtask), inMemoryTaskManager.getSubtasks(), "списки подзадач должны совпадать");
    }

    @Test
    @DisplayName("должен обновить задачу")
    void shouldUpdateTask() {
        task = new Task("taskTitle", "taskD");
        inMemoryTaskManager.addTask(task);
        int id = task.getId();
        task = new Task("changedTitle", "changedTaskD");
        task.setId(id);
        inMemoryTaskManager.updateTask(task);
        Task savedTask = inMemoryTaskManager.getTask(task.getId());
        assertEquals(task, savedTask, "Задачи должны совпадать");
    }

    @Test
    @DisplayName("должен обновить эпик")
    void shouldUpdateEpic() {
        epic = new Epic("epicTitle", "epicD");
        inMemoryTaskManager.addEpic(epic);
        int id = epic.getId();
        epic = new Epic("changedTitle", "changedTaskD");
        epic.setId(id);
        inMemoryTaskManager.updateEpic(epic);
        Task savedEpic = inMemoryTaskManager.getEpic(epic.getId());
        assertEquals(epic, savedEpic, "эпики должны совпадать");
    }

    @Test
    @DisplayName("должен обновить подзадачу")
    void shouldUpdateSubtask() {
        epic = new Epic("epicTitle", "epicD");
        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
        inMemoryTaskManager.addSubtask(subtask);
        int id = subtask.getId();
        int epicId = subtask.getEpicId();
        subtask = new Subtask("changedTitle", "changedTaskD", epicId);
        subtask.setId(id);
        inMemoryTaskManager.updateSubtask(subtask);
        Subtask savedSubtask = inMemoryTaskManager.getSubtask(id);
        assertEquals(subtask, savedSubtask, "подзадачи должны совпадать");

    }

    @Test
    @DisplayName("должен очищать список задач")
    void shouldClearTasks() {
        task = new Task("taskTitle", "taskD");
        inMemoryTaskManager.addTask(task);
        assertFalse(inMemoryTaskManager.tasks.isEmpty(), "не должен быть пустой");
        inMemoryTaskManager.clearTasks();
        assertTrue(inMemoryTaskManager.tasks.isEmpty(), "должен быть пустой");
    }

    @Test
    @DisplayName("должен очищать список эпиков и подзадач")
    void shouldClearEpics() {
        epic = new Epic("epicTitle", "epicD");
        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subtask);
        assertFalse(inMemoryTaskManager.epicTasks.isEmpty(), "не должен быть пустой");
        assertFalse(inMemoryTaskManager.subtasks.isEmpty(), "не должен быть пустой");
        inMemoryTaskManager.clearEpics();
        assertTrue(inMemoryTaskManager.epicTasks.isEmpty(), "должен быть пустой");
        assertTrue(inMemoryTaskManager.subtasks.isEmpty(), "должен быть пустой");
    }

    @Test
    @DisplayName("должен очищать список подзадач и обнулять статусы эпиков")
    void shouldClearSubtasks() {
        epic = new Epic("epicTitle", "epicD");
        inMemoryTaskManager.addEpic(epic);
        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
        inMemoryTaskManager.addSubtask(subtask);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask);
        assertFalse(inMemoryTaskManager.subtasks.isEmpty(), "не должен быть пустой");
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "статусы должны совпадать");
        inMemoryTaskManager.clearSubtasks();
        assertTrue(inMemoryTaskManager.subtasks.isEmpty(), "должен быть пустой");
        assertEquals(TaskStatus.NEW, epic.getStatus(), "статусы должны совпадать");
    }

    @Test
    @DisplayName("должен удалить задачу")
    void shouldRemoveTask() {
        task = new Task("taskTitle", "taskD");
        inMemoryTaskManager.addTask(task);
        assertNotNull(inMemoryTaskManager.getTask(task.getId()), "не должен быть null");
        inMemoryTaskManager.removeTask(task.getId());
        assertNull(inMemoryTaskManager.getTask(task.getId()), "должен быть null");
    }

    @Test
    @DisplayName("должен удалить эпик и его подзадачи")
    void shouldRemoveEpic() {
        epic = new Epic("epicTitle", "epicD");
        inMemoryTaskManager.addEpic(epic);
        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
        inMemoryTaskManager.addSubtask(subtask);
        assertNotNull(inMemoryTaskManager.getEpic(epic.getId()), "не должен быть null");
        assertNotNull(inMemoryTaskManager.getEpicSubtasks(epic), "не должен быть null");
        inMemoryTaskManager.removeEpic(epic.getId());
        assertNull(inMemoryTaskManager.getEpic(epic.getId()), "должен быть null");
        assertNull(inMemoryTaskManager.getEpicSubtasks(epic), "должен быть null");
    }

    @Test
    @DisplayName("должен удалить подзадачу и обновить статус эпика")
    void shouldRemoveSubtask() {
        epic = new Epic("epicTitle", "epicD");
        inMemoryTaskManager.addEpic(epic);
        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
        inMemoryTaskManager.addSubtask(subtask);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "статусы должны совпадать");
        assertNotNull(inMemoryTaskManager.getSubtask(subtask.getId()), "не должен быть null");
        inMemoryTaskManager.removeSubtask(subtask.getId());
        assertEquals(TaskStatus.NEW, epic.getStatus(), "статусы должны совпадать");
        assertNull(inMemoryTaskManager.getSubtask(subtask.getId()), "должен быть null");
    }

    private static class EmptyHistoryManager implements HistoryManager {
        @Override
        public void add(Task task) {

        }

        @Override
        public void remove(int id) {

        }

        @Override
        public List<Task> getHistory() {
            return List.of();
        }
    }
}