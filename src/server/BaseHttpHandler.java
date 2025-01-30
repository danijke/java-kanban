package server;

import com.sun.net.httpserver.*;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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



}
