package queue;

public class MyQueueTest {

    public static void test(Queue queue) {
        for (int i = 0; i < 3; i++) {
            queue.enqueue("q" + i);
        }
        System.out.println("added " + 3 + " elements");
        for (int i = 0; i < 2; i++) {
            Object result = queue.dequeue();
            System.out.println("Removed element: " + result + " " + queue.size() + " elements left");
        }
        for (int i = 0; i < 5; i++) {
            queue.enqueue("s" + i);
        }
        System.out.println(queue.toStr());
        System.out.println("added " + 5 + " elements");
        while (!queue.isEmpty()) {
            Object result = queue.dequeue();
            System.out.println("Removed element: " + result + " " + queue.size() + " elements left");
        }
    }

    public static void main(String[] args) {
        Queue queue1 = new LinkedQueue();
        Queue queue2 = new ArrayQueue();
        test(queue1);
        System.out.println();
        test(queue2);
    }
}
