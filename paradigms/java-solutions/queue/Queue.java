package queue;

    /*
    Model: a[0]..a[size-1] - индексация для пользователя с нуля.
    Invariant: for i = 0..size-1: a[i] != null

    Let immutable(l..r): for i=l..r: a'[i] == a[i]
     */

public interface Queue {

    /*
    Pred: element != null
    Post: size' == size + 1 && a[size] == element && immutable(0..size-1)
     */
    void enqueue(Object element);

    /*
    Pred: size > 0
    Post: R == a[0] && size' == size && immutable(0..size'-1)
     */
    Object element();

    /*
    Pred: size > 0
    Post: size' == size - 1 && immutable(1..size') && R == a[0]
     */
    Object dequeue();

    /*
    Pred: true
    Post: R == size && size' == size && immutable(0..size')
     */
    int size();

    /*
    Pred: true
    Post: R == (size == 0) && size' = size && immutable(0..size'-1)
     */
    boolean isEmpty();

    /*
    Pred: true
    Post: size' == 0
     */
    void clear();

    /*
    Pred: element != null
    Post: R == count -- number of i: a[i] == element && size' == size && immutable(0..size-1)
     */
    int count(Object element);

    /*
    Pred: true
    Post: R == "[a[0], a[1], ..., a[size-1]]" && size' == size && immutable(0..size-1)
     */
    String toStr();

}
