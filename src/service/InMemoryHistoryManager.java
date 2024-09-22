package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    List<Task> historyOfView = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyOfView.size() == 10) {
            historyOfView.removeFirst();
        }
        historyOfView.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyOfView;
    }
}
