package server;

import com.google.gson.*;
import com.google.gson.stream.*;
import com.sun.net.httpserver.*;
import model.*;
import service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static TaskManager taskManager;

    public BaseHttpHandler() {
        taskManager = HttpTaskServer.getTaskManager();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Optional<Integer> id = getIdFromPath(exchange);
        switch (method) {
            case "GET":
                handleGet(exchange, id);
                break;
            case "POST":
                handlePost(exchange, id);
                break;
            case "DELETE":
                handleDelete(exchange, id);
                break;
            default:
                sendNotFound(exchange, "эндпоинт не поддерживается");
        }

    }

    protected abstract void handleGet(HttpExchange exchange, Optional<Integer> id) throws IOException;
    protected abstract void handlePost(HttpExchange exchange, Optional<Integer> id) throws IOException;
    protected abstract void handleDelete(HttpExchange exchange, Optional<Integer> id) throws IOException;

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
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


    private Optional<Integer> getIdFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        try {
            return Optional.of(Integer.parseInt(parts[parts.length-1]));
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
            return new JsonPrimitive(duration.toString());
        }

        @Override
        public Duration deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return Duration.parse(json.getAsString());
        }
    }

    public static class SubtaskAdapter implements JsonSerializer<Subtask> {
        @Override
        public JsonElement serialize(Subtask subtask, Type type, JsonSerializationContext context) {
            JsonObject jsonObject = context.serialize((Task) subtask).getAsJsonObject(); // Сериализуем Task
            jsonObject.addProperty("epicId", subtask.getEpicId()); // Добавляем epicId в конец
            return jsonObject;
        }
    }


    static class JsonUtils {
        private static final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        public static String toJson(Task task) {
            return gson.toJson(task);
        }

        public static String toJson(Subtask task) {
            return gson.toJson(task);
        }

        public static <T extends Task> T fromJson(String json, Class<T> task) {
            return gson.fromJson(json, task);
        }
    }





}
