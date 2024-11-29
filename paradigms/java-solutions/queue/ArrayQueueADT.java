package queue;

import java.util.Arrays;


    /*
    Model: a[0]..a[size-1] - индексация для пользователя с нуля.
    Invariant: for i = 0..size-1: a[i] != null

    Let immutable(l..r): for i=l..r: a'[i] == a[i]
     */

public class ArrayQueueADT {

    private Object[] elements = new Object[5];

    private int size;
    private int head = -1;
    private int tail = -1;

    private static void ensureCapacity(ArrayQueueADT queue, int size) {
        if (size >= queue.elements.length) {
            Object[] ensuredQueue = new Object[size * 2];
            if (queue.head > queue.tail) {
                System.arraycopy(queue.elements, queue.head, ensuredQueue, 0, queue.elements.length - queue.head);
                System.arraycopy(queue.elements, 0, ensuredQueue, queue.elements.length - queue.head, queue.tail + 1);
            } else {
                System.arraycopy(queue.elements, queue.head, ensuredQueue, 0, queue.size);
            }
            queue.elements = ensuredQueue;
            queue.head = 0;
            queue.tail = size(queue) - 1;
        }
    }


    /*
    Pred: queue != null
    Post: R == size && size' == size && immutable(0..size')
     */
    public static int size(ArrayQueueADT queue) {
        assert queue != null;
        return queue.size;
    }


    /*
    Pred: element != null && queue != null
    Post: size' == size + 1 && a[size] == element && immutable(0..size-1)
     */
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert queue != null;
        assert element != null;
        ensureCapacity(queue,queue.size + 1);
        if (queue.head < 0) {
            queue.head = 0;
        }
        queue.tail = (queue.tail + 1) % queue.elements.length;
        queue.elements[queue.tail] = element;
        queue.size++;
    }

    /*
    Pred: size > 0 && queue != null
    Post: size' == size - 1 && immutable(1..size') && R == a[0]
     */
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue != null;
        assert queue.size > 0;
        queue.size--;
        Object result = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.head = (queue.head + 1) % queue.elements.length;
        return result;
    }

    /*
    Pred: size > 0 && queue != null
    Post: R == a[0] && size' == size && immutable(0..size'-1)
     */
    public static Object element(ArrayQueueADT queue) {
        assert queue.size > 0;
        return queue.elements[queue.head];
    }

    /*
    Pred: queue != null
    Post: R == (size == 0) && size' = size && immutable(0..size'-1)
     */
    public static boolean isEmpty(ArrayQueueADT queue) {
        assert queue != null;
        return queue.size == 0;
    }

    /*
    Pred: queue != null
    Post: size' == 0
     */
    public static void clear(ArrayQueueADT queue) {
        assert queue != null;
        queue.elements = new Object[5];
        queue.head = -1;
        queue.tail = -1;
        queue.size = 0;
    }


    /*
    Pred: queue != null
    Post: R == "[a[0], a[1], ..., a[size-1]]" && size' == size && immutable(0..size-1)
     */
    public static String toStr(ArrayQueueADT queue) {
        assert queue != null;
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < queue.size; i++) {
            result.append(queue.elements[(queue.head + i) % queue.elements.length]);
            if (i + 1 != queue.size) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }

}
