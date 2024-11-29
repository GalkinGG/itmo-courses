package queue;

public class ArrayQueue extends AbstractQueue{

    private Object[] elements = new Object[5];
    private int head = -1;
    private int tail = -1;

    private void ensureCapacity() {
        if (size + 1 >= elements.length) {
            Object[] ensuredQueue = new Object[size * 2];
            if (head > tail) {
                System.arraycopy(elements, head, ensuredQueue, 0, elements.length - head);
                System.arraycopy(elements, 0, ensuredQueue, elements.length - head, tail + 1);
            } else {
                System.arraycopy(elements, head, ensuredQueue, 0, size);
            }
            elements = ensuredQueue;
            head = 0;
            tail = size - 1;
        }
    }


    @Override
    protected void enqueueImpl(Object element) {
        ensureCapacity();
        if (head < 0) {
            head = 0;
        }
        tail = (tail + 1) % elements.length;
        elements[tail] = element;
    }

    @Override
    protected void dequeueImpl() {
        elements[head] = null;
        head = (head + 1) % elements.length;
    }

    @Override
    protected Object elementImpl() {
        return elements[head];
    }

    @Override
    protected void clearImpl() {
        elements = new Object[5];
        head = -1;
        tail = -1;
    }

    @Override
    protected Object[] toArray() {
        Object[] queue = new Object[size];
        if (head > tail && size > 0) {
            System.arraycopy(elements, head, queue, 0, elements.length - head);
            System.arraycopy(elements, 0, queue, elements.length - head, tail + 1);
        } else {
            System.arraycopy(elements, head, queue, 0, size());
        }
        return queue;
    }
}
