package server;

import com.sun.net.httpserver.HttpServer;
import service.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer server;
    private static final TaskManager taskManager = Managers.getFileManager(Paths.get("data.csv"));

    public static void main(String[] args) throws IOException {
        start();
    }

    private static void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            System.err.println("Ошибка при создании сервера");
        }
        server.createContext("/tasks", new TaskHandler());
        /*server.createContext("/subtasks", new SubtasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());*/
        server.start();
        System.out.println("Сервер создан. Порт: " + PORT);
    }

    public static void stop() {
        server.stop(0);
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }
}
