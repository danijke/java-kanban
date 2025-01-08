//package service;
//
//import model.*;
//import org.junit.jupiter.api.*;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("История просмотров")
//class InMemoryHistoryManagerTest {
//    InMemoryHistoryManager historyManager;
//    Task task;
//    Epic epic;
//    Subtask subtask;
//
//    @BeforeEach
//    void init() {
//        historyManager = new InMemoryHistoryManager();
//        task = new Task("taskTitle", "taskD");
//        task.setId(0);
//        epic = new Epic("epicTitle", "epicD");
//        epic.setId(1);
//        subtask = new Subtask("subtaskTitle", "subtaskD", epic.getId());
//        subtask.setId(2);
//    }
//
//    @Test
//    @DisplayName("должен добавлять задачи в список просмотренных")
//    void shouldAddTaskInHistory() {
//        historyManager.add(task);
//        historyManager.add(epic);
//        historyManager.add(subtask);
//        assertEquals(List.of(task, epic, subtask), historyManager.getHistory());
//    }
//
//    @Test
//    @DisplayName("должен удалять задачи из список просмотренных")
//    void shouldRemoveTaskInHistory() {
//        historyManager.add(task);
//        historyManager.add(epic);
//        historyManager.add(subtask);
//        historyManager.remove(task.getId());
//        assertEquals(List.of(epic, subtask), historyManager.getHistory());
//        historyManager.remove(epic.getId());
//        assertEquals(List.of(subtask), historyManager.getHistory());
//        historyManager.remove(subtask.getId());
//        assertEquals(List.of(), historyManager.getHistory());
//        historyManager.add(task);
//        assertEquals(List.of(task), historyManager.getHistory());
//    }
//}