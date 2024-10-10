package service;

import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Менеджер объектов")
class ManagersTest {
    TaskManager taskManager;
    HistoryManager historyManager;


    @Test
    @DisplayName("должен возвращать проинициализированный объект InMemoryTaskManager")
    void shouldGetDefault() {
        Task task = new Task("taskTitle", "taskD");
        taskManager = Managers.getDefault();
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTask(task.getId()));

    }

    @Test
    @DisplayName("должен возвращать проинициализированный объект InMemoryHistoryManager")
    void getDefaultHistory() {
        Task task = new Task("taskTitle", "taskD");
        historyManager = Managers.getDefaultHistory();
        historyManager.add(task);
        historyManager.getHistory();
        assertEquals(List.of(task), historyManager.getHistory());
    }
}