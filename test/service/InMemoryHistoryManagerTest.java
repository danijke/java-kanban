package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("История просмотров")
class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;
    Task task;
    Epic epic;
    Subtask subtask;



    @Test
    @DisplayName("должен добавлять задачи в список просмотренных")
    void shouldAddTaskInHistory() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("taskTitle", "taskD");
        epic = new Epic("epicTitle", "epicD");
        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        assertTrue(historyManager.historyOfView.contains(task));
        assertTrue(historyManager.historyOfView.contains(epic));
        assertTrue(historyManager.historyOfView.contains(subtask));
    }

}