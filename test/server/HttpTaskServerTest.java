package server;

import model.*;
import org.junit.jupiter.api.*;
import service.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static server.BaseHttpHandler.JsonUtils.*;

public class HttpTaskServerTest {
    TaskManager manager;
    HttpClient client;

    Task task;
    Epic epic;
    Subtask subtask;

    private HttpRequest createRequest(String method, String endPoint, String text) {
        URI url = URI.create("http://localhost:8080" + endPoint);
        return HttpRequest.newBuilder()
                .uri(url)
                .method(method, HttpRequest.BodyPublishers.ofString(text))
                .header("Accept", "application/json")
                .build();
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Epic createEpic() {
        return new Epic("Epic 1", "Description 1");
    }

    private Task createTask() {
        return new Task("Task 1", "Description 1", LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 10)), Duration.ofMinutes(50));
    }

    private Subtask createSubtask(int epicId) {
        return new Subtask("Subtask 1", "Description 1", epicId, LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)), Duration.ofMinutes(50));
    }

    private Subtask createAnSubtask(int epicId) {
        return new Subtask("Subtask 2", "Description 2", epicId, LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)), Duration.ofMinutes(50));
    }

    @BeforeEach
    public void init() {
        manager = Managers.getDefault();
        HttpTaskServer.setDefaultManager(manager);
        client = HttpClient.newHttpClient();

        HttpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stop();
    }

    @Test
    @DisplayName("должен добавлять задачи, эпики и подзадачи")
    public void shouldAddEntities() {
        task = createTask();
        task.setId(1);
        HttpRequest request = createRequest("POST", "/tasks", toJson(task));
        HttpResponse<String> response = send(request);

        assertEquals(201, response.statusCode());
        assertEquals(List.of(task), manager.getTasks(), "задача должна быть добавлена и совпадать");

        epic = createEpic();
        epic.setId(2);

        request = createRequest("POST", "/epics", toJson(epic));
        response = send(request);
        assertEquals(201, response.statusCode());
        assertEquals(List.of(epic), manager.getEpics(), "эпик должен быть добавлен и совпадат");

        subtask = createSubtask(epic.getId());
        subtask.setId(3);
        request = createRequest("POST", "/subtasks", toJson(subtask));
        response = send(request);
        assertEquals(201, response.statusCode());
        assertEquals(List.of(subtask), manager.getSubtasks(), "подзадача должна быть добавлена и совпадать");
    }

    @Test
    @DisplayName("должен получать задачи, эпики и подзадачи")
    public void shouldGetEntities() {
        shouldAddEntities();

        HttpRequest request = createRequest("GET", "/tasks/" + task.getId(), "");
        HttpResponse<String> response = send(request);
        Task anTask = fromJson(response.body(), Task.class);
        anTask.setId(1);

        assertEquals(200, response.statusCode());
        assertEquals(task, anTask, "задача должна совпадать");

        request = createRequest("GET", "/epics/" + epic.getId(), "");
        response = send(request);
        Epic anEpic = fromJson(response.body(), Epic.class);
        anEpic.setId(2);
        assertEquals(200, response.statusCode());
        assertEquals(epic, anEpic, "эпик должен совпадать");

        request = createRequest("GET", "/subtasks/" + subtask.getId(), "");
        response = send(request);
        Subtask anSubtask = fromJson(response.body(), Subtask.class);
        anSubtask.setId(3);
        assertEquals(200, response.statusCode());
        assertEquals(anSubtask, subtask, "подзадача должна совпадать");
    }

    @Test
    @DisplayName("должен получать историю задач")
    public void shouldGetHistory() {
        shouldAddEntities();
        task = manager.getTask(1);

        HttpRequest request = createRequest("GET", "/history", "");
        HttpResponse<String> response = send(request);
        assertEquals(200, response.statusCode());
        assertEquals(List.of(task), manager.getHistory(), "списки историй должны совпадать");
    }

    @Test
    @DisplayName("должен получать отсортированный список")
    public void shouldGetPrioritized() {
        shouldAddEntities();
        Subtask anSubtask = createAnSubtask(epic.getId());
        manager.addSubtask(anSubtask);

        HttpRequest request = createRequest("GET", "/prioritized", "");
        HttpResponse<String> response = send(request);
        assertEquals(200, response.statusCode());
        assertEquals(List.of(anSubtask, task, subtask), manager.getPrioritizedTasks(), "списки отсортированных задач должны совпадать");
    }
}
