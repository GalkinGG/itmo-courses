package info.kgeorgiy.ja.galkin.iterative;


import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ParallelMapperImpl implements ParallelMapper {

    /**
     * The queue to which all tasks for threads are added
     */
    private final SyncQueue taskQueue;

    /**
     * The list in which the running threads are stored
     */
    private final List<Thread> threadList;

    private RuntimeException exception = null;

    /**
     * Creates a ParallelMapper that uses the specified number of threads
     *
     * @param threads count of threads
     */
    public ParallelMapperImpl(int threads) {
        taskQueue = new SyncQueue();
        threadList = IntStream.range(0, threads).mapToObj(i -> getWorker()).collect(Collectors.toList());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> items) throws InterruptedException {
        List<R> res = new ArrayList<>(Collections.nCopies(items.size(), null));
        Counter counter = new Counter(items.size());
        IntStream.range(0, items.size()).forEach(i -> taskQueue.addTask(() -> {
            RuntimeException e = null;
            try {
                res.set(i, f.apply(items.get(i)));
            } catch (RuntimeException ex) {
                e = ex;
            }
            checkException(e);
            counter.decrement();
        }));
        counter.waitZero();
        if (exception != null) {
            throw exception;
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        for (Thread thread : threadList) {
            try {
                if (!thread.isInterrupted()) {
                    thread.interrupt();
                    thread.join();
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Suppresses the passed exception
     *
     * @param e Runtime exception that needs to be suppressed
     */
    private synchronized void checkException(RuntimeException e) {
        if (e != null) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }
    }

    /**
     * Creates a thread that executes tasks from a queue
     *
     * @return started thread that executes tasks from a queue
     */
    private Thread getWorker() {
        Thread worker = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    taskQueue.removeFirstTask().run();
                }
            } catch (InterruptedException ignored) {
            }
        });
        worker.start();
        return worker;
    }

    /**
     * Queue with synchronized operations addTask/removeFirst.
     */
    private static class SyncQueue extends ArrayDeque<Runnable> {

        private synchronized void addTask(Runnable task) {
            addLast(task);
            notify();
        }

        private synchronized Runnable removeFirstTask() throws InterruptedException {
            while (isEmpty()) {
                wait();
            }
            return removeFirst();
        }
    }

    /**
     * Counter with synchronized operations decrement/waitZero.
     */
    private static class Counter {
        private int c;

        private Counter(int c) {
            this.c = c;
        }

        private synchronized void decrement() {
            if (--c == 0) {
                notify();
            }
        }

        private synchronized void waitZero() throws InterruptedException {
            while (c != 0) {
                wait();
            }
        }

    }
}
