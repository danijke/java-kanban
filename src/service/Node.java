package service;

import model.Task;

public class Node {
    public Task data;
    public Node prev;
    public Node next;

    public Node(Node prev, Task data, Node next) {
        this.data = data;
        this.prev = prev;
        this.next = next;
    }
}
