package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final Node head = new Node(null, null, null);
    private final Node tail = new Node(head, null, null);
    HashMap<Integer, Node> historyMap = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        int id = task.getId();
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
        }
        Node newNode = linkLast(task);
        historyMap.put(id, newNode);
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    private List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();
        Node currentNode = head.next;
        while (currentNode.data != null) {
            historyList.add(currentNode.data);
            currentNode = currentNode.next;

        }
        return historyList;
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(tail.prev, task, tail);
        tail.prev.next = newNode;
        tail.prev = newNode;
        return newNode;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}
