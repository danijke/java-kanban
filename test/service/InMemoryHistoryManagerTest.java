package service;

import model.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("История просмотров")
class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
    }

    private Epic createEpic() {
        Epic epic = new Epic(2,"Epic 1",TaskStatus.NEW, "Description 1", LocalDateTime.now(), Duration.ZERO);
        historyManager.add(epic);
        return epic;
    }

    private Task createTask() {
        Task task = new Task(1,"Task 1", TaskStatus.NEW, "Description 1", LocalDateTime.now(), Duration.ZERO);
        historyManager.add(task);
        return task;
    }

    private Subtask createSubtask(int epicId) {
        Subtask subtask = new Subtask(3,"Subtask 1",TaskStatus.NEW, "Description 1", epicId, LocalDateTime.now(), Duration.ZERO);
        historyManager.add(subtask);
        return subtask;
    }

    @Test
    @DisplayName("должен добавлять задачи в список просмотренных")
    void shouldAddTaskInHistory() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());
        assertEquals(List.of(task, epic, subtask), historyManager.getHistory(),"списки должны совпадать");

        historyManager.add(task);
        assertEquals(List.of(epic, subtask, task), historyManager.getHistory(),"списки должны совпадать");
    }

    @Test
    @DisplayName("должен удалять задачи из список просмотренных")
    void shouldRemoveTaskInHistory() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());

        historyManager.remove(epic.getId());
        assertEquals(List.of(task, subtask), historyManager.getHistory(), "списки должны совпадать");

        historyManager.remove(task.getId());
        assertEquals(List.of(subtask), historyManager.getHistory(), "списки должны совпадать");

        historyManager.remove(subtask.getId());
        assertTrue(historyManager.getHistory().isEmpty(), "список должен быть пуст");

        historyManager.add(task);
        assertEquals(List.of(task), historyManager.getHistory(), "списки должны совпадать");
    }
}