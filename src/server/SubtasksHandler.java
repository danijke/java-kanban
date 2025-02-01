package server;

import com.sun.net.httpserver.*;
import exception.*;
import model.Subtask;

import java.io.*;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromPath(exchange);
        if (id.isPresent()) {
            try {
                sendText(exchange, JsonUtils.toJson(taskManager.getSubtask(id.get())));
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        } else {
            sendText(exchange, JsonUtils.toJson(taskManager.getSubtasks()));
        }
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromPath(exchange);
        try (InputStream inputStream = exchange.getRequestBody()) {
            Optional<Subtask> subtask = parseTask(inputStream, Subtask.class);
            if (subtask.isPresent()) {
                try {
                    if (id.isPresent()) {
                        taskManager.updateSubtask(subtask.get());
                        sendPostText(exchange, "подзадача успешно обновлена.");
                    } else {
                        try {
                            taskManager.addSubtask(subtask.get());
                            sendPostText(exchange, "подзадача успешно добавлена.");
                        } catch (InteractedException e) {
                            sendHasInteractions(exchange);
                        }
                    }
                } catch (NotFoundException e) {
                    sendNotFound(exchange, e.getMessage());
                }
            } else {
                sendBadRequest(exchange);
            }
        }
    }

    @Override
    public void handleDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromPath(exchange);
        if (id.isPresent()) {
            taskManager.removeSubtask(id.get());
            sendText(exchange, "подзадача успешно удалена.");
        } else {
            sendBadRequest(exchange);
        }
    }
}
