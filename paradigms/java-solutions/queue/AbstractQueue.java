package queue;

import java.util.Arrays;

public abstract class AbstractQueue implements Queue{
    protected int size;

    public int size() {
        return size;
    }


    public boolean isEmpty() {
        return size == 0;
    }


    public Object element() {
        assert size > 0;
        return elementImpl();
    }

    protected abstract Object elementImpl();

    public void enqueue(Object element) {
        assert element != null;
        enqueueImpl(element);
        size++;
    }

    protected abstract void enqueueImpl(Object element);


    public Object dequeue() {
        assert size > 0;
        Object res = element();
        size--;
        dequeueImpl();
        return res;
    }
    protected abstract void dequeueImpl();

    public void clear() {
        size = 0;
        clearImpl();
    }

    protected abstract void clearImpl();

    public int count(Object element) {
        assert element != null;
        Object currentElement;
        int res = 0;
        for (int i = 0; i < size; i++) {
            currentElement = dequeue();
            if (currentElement.equals(element)) {
                res++;
            }
            enqueue(currentElement);
        }
        return res;
    }

    protected Object[] toArray() {
        Object[] queue = new Object[size];
        int i = 0;
        Object currentElement;
        while (i < size) {
            currentElement = dequeue();
            queue[i] = currentElement;
            enqueue(currentElement);
            i++;
        }
        return queue;
    }

    public String toStr() {
        return Arrays.toString(toArray());
    }
}
