package queue;

import java.util.Arrays;


    /*
    Model: a[0]..a[size-1] - индексация для пользователя с нуля.
    Invariant: for i = 0..size-1: a[i] != null

    Let immutable(l..r): for i=l..r: a'[i] == a[i]
     */

public class ArrayQueueModule {

    private static Object[] elements = new Object[5];

    private static int size;
    private static int head = -1;
    private static int tail = -1;

    private static void ensureCapacity(int size) {
        if (size >= elements.length) {
            Object[] ensuredQueue = new Object[size * 2];
            if (head > tail) {
                System.arraycopy(elements, head, ensuredQueue, 0, elements.length - head);
                System.arraycopy(elements, 0, ensuredQueue, elements.length - head, tail + 1);
            } else {
                System.arraycopy(elements, head, ensuredQueue, 0, size());
            }
            elements = ensuredQueue;
            head = 0;
            tail = size() - 1;
        }
    }


    /*
    Pred: true
    Post: R == size && size' == size && immutable(0..size')
     */
    public static int size() {
        return size;
    }


    /*
    Pred: element != null
    Post: size' == size + 1 && a[size] == element && immutable(0..size-1)
     */
    public static void enqueue(Object element) {
        assert element != null;
        ensureCapacity(size + 1);
        if (head < 0) {
            head = 0;
        }
        tail = (tail + 1) % elements.length;
        elements[tail] = element;
        size++;
    }

    /*
    Pred: size > 0
    Post: size' == size - 1 && immutable(1..size') && R == a[0]
     */
    public static Object dequeue() {
        assert size > 0;
        size--;
        Object result = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        return result;
    }

    /*
    Pred: size > 0
    Post: R == a[0] && size' == size && immutable(0..size'-1)
     */
    public static Object element() {
        assert size > 0;
        return elements[head];
    }

    /*
    Pred: true
    Post: R == (size == 0) && size' = size && immutable(0..size'-1)
     */
    public static boolean isEmpty() {
        return size == 0;
    }

    /*
    Pred: true
    Post: size' == 0
     */
    public static void clear() {
        elements = new Object[5];
        head = -1;
        tail = -1;
        size = 0;
    }

    /*
    Pred: true
    Post: R == "[a[0], a[1], ..., a[size-1]]" && size' == size && immutable(0..size-1)
     */
    public static String toStr() {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            result.append(elements[(head + i) % elements.length]);
            if (i + 1 != size) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }
}
