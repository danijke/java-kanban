package server;

import com.sun.net.httpserver.*;

import java.io.IOException;

public class HistoryHandler  extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        sendText(exchange, JsonUtils.toJson(taskManager.getHistory()));
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
