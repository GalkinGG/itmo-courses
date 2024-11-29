package queue;

public class LinkedQueue extends AbstractQueue {

    private static class Node {
        private final Object element;
        private Node next;

        public Node(Object element) {
            this.element = element;
            this.next = null;
        }
    }
    private Node head, tail;

    @Override
    protected void enqueueImpl(Object element) {
        Node previousElement = tail;
        tail = new Node(element);
        if (size == 0) {
            head = tail;
        } else {
            previousElement.next = tail;
        }
    }

    @Override
    protected void dequeueImpl() {
        head = head.next;
    }

    @Override
    protected Object elementImpl() {
        return head.element;
    }

    @Override
    protected void clearImpl() {
        head = null;
        tail = null;
    }

    @Override
    protected Object[] toArray() {
        Object[] queue = new Object[size];
        Node currentNode = head;
        int i = 0;
        while (i < size) {
            queue[i] = currentNode.element;
            i++;
            currentNode = currentNode.next;
        }
        return queue;
    }
}
