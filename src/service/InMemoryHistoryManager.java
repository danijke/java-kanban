package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Node tail = new Node(null, null, null);
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
        Node currentNode = tail.prev;

        while (currentNode != null) {
            historyList.add(currentNode.data);
            currentNode = currentNode.prev;
        }
        Collections.reverse(historyList);
        return historyList;
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(tail.prev, task, tail);
        if (tail.prev != null) {
            tail.prev.next = newNode;
        }
        tail.prev = newNode;
        return newNode;
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else {
            node.next.prev = null;
        }
    }
}
