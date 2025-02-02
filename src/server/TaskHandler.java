package server;

import com.sun.net.httpserver.*;
import exception.*;
import model.Task;

import java.io.*;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromPath(exchange);
        if (id.isPresent()) {
            try {
                sendText(exchange, JsonUtils.toJson(taskManager.getTask(id.get())));
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        } else {
            sendText(exchange, JsonUtils.toJson(taskManager.getTasks()));
        }
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromPath(exchange);
        try (InputStream inputStream = exchange.getRequestBody()) {
            Optional<Task> task = parseTask(inputStream, Task.class);
            if (task.isPresent()) {
                if (id.isPresent()) {
                    taskManager.updateTask(task.get());
                    sendPostText(exchange, "задача успешно обновлена.");
                } else {
                    try {
                        taskManager.addTask(task.get());
                        sendPostText(exchange, "задача успешно добавлена.");
                    } catch (InteractedException e) {
                        sendHasInteractions(exchange);
                    }
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
            taskManager.removeTask(id.get());
            sendText(exchange, "задача успешно удалена.");
        } else {
            sendBadRequest(exchange);
        }
    }
}