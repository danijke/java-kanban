package server;

import com.sun.net.httpserver.*;
import model.Task;
import service.TaskManager;

import java.io.IOException;

public class BaseHttpHandler implements HttpHandler {
    static TaskManager taskManager;

    public BaseHttpHandler() {
        taskManager = HttpTaskServer.getTaskManager();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }


}
