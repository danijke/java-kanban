package server;

import com.sun.net.httpserver.*;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        sendText(exchange, JsonUtils.toJson(taskManager.getPrioritizedTasks()));
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        sendNotFound(exchange, "данный эндпоинт не поддерживается");
    }

    @Override
    public void handleDelete(HttpExchange exchange) throws IOException {
        sendNotFound(exchange, "данный эндпоинт не поддерживается");
    }
}
