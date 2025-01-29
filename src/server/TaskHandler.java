package server;

import com.sun.net.httpserver.*;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handleGet(HttpExchange exchange, Optional<Integer> id) throws IOException {
        id.ifPresentOrElse(
                (task) -> {
                    task = TaskManager::getTask;

                },
                ()-> {}
        );

    }

    @Override
    public void handlePost(HttpExchange exchange, Optional<Integer> id) throws IOException {

    }

    @Override
    public void handleDelete(HttpExchange exchange, Optional<Integer> id) throws IOException {

    }
}
//todo добавить gson как библиотеку
//todo реализовать методы handle в наследниках~
//todo написать методы сериализации и десиарилизации gson для каждого типа задач
//todo пробросить NotFoundException из TaskManager при отсутсвии задачи и обработать их в обработчиках(возможно добавить приватный метод)