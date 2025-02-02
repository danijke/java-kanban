package server;

import com.sun.net.httpserver.*;
import exception.*;
import model.Epic;

import java.io.*;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromPath(exchange);
        System.out.println("обращение гет к эпику");
        if (id.isPresent()) {
            System.out.println(id.get() + " ид есть");
            if (exchange.getRequestURI().getPath().contains("/subtasks")) {
                System.out.println("условие сработало");
                sendText(exchange, JsonUtils.toJson(taskManager.getEpicSubtasks(id.get())));
            } else {
                System.out.println("условие не сработало");
                try {
                    sendText(exchange, JsonUtils.toJson(taskManager.getEpic(id.get())));
                } catch (NotFoundException e) {
                    sendNotFound(exchange, e.getMessage());
                }
            }
        } else {
            sendText(exchange, JsonUtils.toJson(taskManager.getEpics()));
        }
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        System.out.println("обращение к методу POST EpicHandler");
        try (InputStream inputStream = exchange.getRequestBody()) {
            Optional<Epic> epic = parseTask(inputStream, Epic.class);
            if (epic.isPresent()) {
                try {
                    taskManager.addEpic(epic.get());
                    sendPostText(exchange, "эпик успешно добавлен.");
                } catch (InteractedException e) {
                    sendHasInteractions(exchange);
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
            taskManager.removeEpic(id.get());
            sendText(exchange, "эпик успешно удален.");
        } else {
            sendBadRequest(exchange);
        }
    }
}
