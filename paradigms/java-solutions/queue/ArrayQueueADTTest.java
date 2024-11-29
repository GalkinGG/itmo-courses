package queue;

public class ArrayQueueADTTest {
    public static void test(ArrayQueueADT queue) {
        for (int i = 0; i < 3; i++) {
            ArrayQueueADT.enqueue(queue,"q" + i);
        }
        System.out.println("added " + 3 + " elements");
        for (int i = 0; i < 2; i++) {
            Object result = ArrayQueueADT.dequeue(queue);
            System.out.println("Removed element: " + result + " " + ArrayQueueADT.size(queue) + " elements left");
        }
        for (int i = 0; i < 5; i++) {
            ArrayQueueADT.enqueue(queue,"s" + i);
        }
        System.out.println("added " + 5 + " elements");
        System.out.println(ArrayQueueADT.toStr(queue));
        while (!ArrayQueueADT.isEmpty(queue)) {
            Object result = ArrayQueueADT.dequeue(queue);
            System.out.println("Removed element: " + result + " " + ArrayQueueADT.size(queue) + " elements left");
        }
    }

    public static void main(String[] args) {
        ArrayQueueADT queue1 = new ArrayQueueADT();
        ArrayQueueADT queue2 = new ArrayQueueADT();
        test(queue1);
        System.out.println();
        test(queue2);
    }
}
