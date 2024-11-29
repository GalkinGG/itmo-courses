package queue;

public class ArrayQueueModuleTest {

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            ArrayQueueModule.enqueue("q" + i);
        }
        System.out.println("added " + 3 + " elements");
        for (int i = 0; i < 2; i++) {
            Object result = ArrayQueueModule.dequeue();
            System.out.println("Removed element: " + result + " " + ArrayQueueModule.size() + " elements left");
        }
        for (int i = 0; i < 5; i++) {
            ArrayQueueModule.enqueue("s" + i);
        }
        System.out.println("added " + 5 + " elements");
        System.out.println(ArrayQueueModule.toStr());
        while (!ArrayQueueModule.isEmpty()) {
            Object result = ArrayQueueModule.dequeue();
            System.out.println("Removed element: " + result + " " + ArrayQueueModule.size() + " elements left");
        }
    }
}
