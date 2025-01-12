package service;

import model.Task;
import org.junit.jupiter.api.*;

import java.util.List;

@DisplayName("InMemoryTaskManager")
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void initTaskManager() {
        EmptyHistoryManager historyManager = new EmptyHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
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