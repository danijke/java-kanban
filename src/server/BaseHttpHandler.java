package server;

import com.google.gson.*;
import com.sun.net.httpserver.*;
import exception.ManagerSaveException;
import model.*;
import service.TaskManager;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;

    public BaseHttpHandler() {
        taskManager = HttpTaskServer.getTaskManager();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendNotFound(exchange, "данный эндпоинт не поддерживается");
            }
        } catch (ManagerSaveException e) {
            sendServerErr(exchange);
        }

    }

    protected abstract void handleGet(HttpExchange exchange) throws IOException;

    protected abstract void handlePost(HttpExchange exchange) throws IOException;

    protected abstract void handleDelete(HttpExchange exchange) throws IOException;

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendPostText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "text;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        String text = "Создаваемая задача пересекается по времени с существующими";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "text;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendBadRequest(HttpExchange h) throws IOException {
        String text = "Некорректный запрос";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "text;charset=utf-8");
        h.sendResponseHeaders(400, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendServerErr(HttpExchange h) throws IOException {
        String text = "Ошибка при сохранении данных менеджера в файл";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "text;charset=utf-8");
        h.sendResponseHeaders(500, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected <T extends Task> Optional<T> parseTask(InputStream inputStream, Class<T> type) throws IOException {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        boolean requiredStrings = body.contains("title") && body.contains("description") && (!type.equals(Subtask.class) || body.contains("epicId"));
        if (requiredStrings) {
            T task = JsonUtils.fromJson(body, type);
            return Optional.of(task);
        } else {
            return Optional.empty();
        }
    }

    protected Optional<Integer> getIdFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        try {
            if (parts.length <= 3) {
                return Optional.of(Integer.parseInt(parts[parts.length - 1]));
            } else {
                return Optional.of(Integer.parseInt(parts[2]));
            }
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public JsonElement serialize(LocalDateTime dateTime, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(dateTime.format(dtf));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString(), dtf);
        }
    }

    static class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
        @Override
        public JsonElement serialize(Duration duration, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(duration.toMinutes());
        }

        @Override
        public Duration deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return Duration.ofMinutes(json.getAsInt());
        }
    }

    static class TaskAdapter implements JsonSerializer<Task>, JsonDeserializer<Task> {
        @Override
        public Task deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String title = jsonObject.get("title").getAsString();
            String description = jsonObject.get("description").getAsString();
            LocalDateTime startTime = context.deserialize(jsonObject.get("startTime"), LocalDateTime.class);
            Duration duration = context.deserialize(jsonObject.get("duration"), Duration.class);
            return new Task(title, description, startTime, duration);
        }

        @Override
        public JsonElement serialize(Task t, Type type, JsonSerializationContext context) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("type", t.getClass().getSimpleName());
            jsonObj.addProperty("id", t.getId());
            jsonObj.addProperty("title", t.getTitle());
            jsonObj.addProperty("status", t.getStatus().toString());
            jsonObj.addProperty("description", t.getDescription());
            jsonObj.add("startTime", context.serialize(t.getStartTime()));
            jsonObj.add("duration", context.serialize(t.getDuration()));
            return jsonObj;
        }
    }

    static class EpicAdapter implements JsonSerializer<Epic>, JsonDeserializer<Epic> {
        @Override
        public Epic deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String title = jsonObject.get("title").getAsString();
            String description = jsonObject.get("description").getAsString();
            return new Epic(title, description);
        }

        @Override
        public JsonElement serialize(Epic t, Type type, JsonSerializationContext context) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("type", t.getClass().getSimpleName());
            jsonObj.addProperty("id", t.getId());
            jsonObj.addProperty("title", t.getTitle());
            jsonObj.addProperty("status", t.getStatus().toString());
            jsonObj.addProperty("description", t.getDescription());
            jsonObj.add("startTime", context.serialize(t.getStartTime()));
            jsonObj.add("duration", context.serialize(t.getDuration()));
            return jsonObj;
        }
    }

    static class SubtaskAdapter implements JsonSerializer<Subtask>, JsonDeserializer<Subtask> {
        @Override
        public Subtask deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String title = jsonObject.get("title").getAsString();
            String description = jsonObject.get("description").getAsString();
            int epicId = jsonObject.get("epicId").getAsInt();
            LocalDateTime startTime = context.deserialize(jsonObject.get("startTime"), LocalDateTime.class);
            Duration duration = context.deserialize(jsonObject.get("duration"), Duration.class);
            return new Subtask(title, description, epicId, startTime, duration);
        }

        @Override
        public JsonElement serialize(Subtask t, Type type, JsonSerializationContext context) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("type", t.getClass().getSimpleName());
            jsonObj.addProperty("id", t.getId());
            jsonObj.addProperty("title", t.getTitle());
            jsonObj.addProperty("status", t.getStatus().toString());
            jsonObj.addProperty("description", t.getDescription());
            jsonObj.addProperty("epicId", t.getEpicId());
            jsonObj.add("startTime", context.serialize(t.getStartTime()));
            jsonObj.add("duration", context.serialize(t.getDuration()));
            return jsonObj;
        }
    }

    static class JsonUtils {
        private static final Gson gson = new GsonBuilder().registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        public static String toJson(Task task) {
            return gson.toJson(task);
        }

        public static String toJson(List<? extends Task> tasks) {
            return gson.toJson(tasks);
        }

        public static <T extends Task> T fromJson(String json, Class<T> type) {
            return gson.fromJson(json, type);
        }
    }
}
