package service;

import org.junit.jupiter.api.*;
import org.mockito.*;

@DisplayName("InMemoryTaskManager")
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Mock
    private InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    void initTaskManager() {
        MockitoAnnotations.openMocks(this);
        taskManager = new InMemoryTaskManager(inMemoryHistoryManager);
    }
}