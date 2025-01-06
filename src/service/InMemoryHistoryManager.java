package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

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
        Node currentNode = head;
        while (currentNode != null) {
            historyList.add(currentNode.data);
            currentNode = currentNode.next;

        }
        return historyList;
    }

    private Node linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        return newNode;
    }

    private void removeNode(Node node) {
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }
    }

    static class Node {
        public Task data;
        public Node prev;
        public Node next;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }
}


