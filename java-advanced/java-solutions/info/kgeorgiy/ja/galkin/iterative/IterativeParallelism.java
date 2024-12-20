package info.kgeorgiy.ja.galkin.iterative;

import info.kgeorgiy.java.advanced.iterative.NewScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IterativeParallelism implements NewScalarIP {

    private final ParallelMapper mapper;

    public IterativeParallelism() {
        mapper = null;
    }

    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator, int step) throws InterruptedException {
        return getResult(threads,
                values,
                stream -> stream.max(comparator).orElse(null),
                stream -> stream.max(comparator).orElse(null),
                step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator, int step) throws InterruptedException {
        return getResult(threads,
                values,
                stream -> stream.min(comparator).orElse(null),
                stream -> stream.min(comparator).orElse(null),
                step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return getResult(threads,
                values,
                stream -> stream.allMatch(predicate),
                stream -> stream.allMatch(it -> it),
                step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return getResult(threads,
                values,
                stream -> stream.anyMatch(predicate),
                stream -> stream.anyMatch(it -> it),
                step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int count(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return getResult(threads,
                values,
                stream -> (int) stream.filter(predicate).count(),
                stream -> stream.mapToInt(i -> i).sum(),
                step);
    }

    /**
     * Returns an array that contains the sizes of the lists processed by the thread.
     *
     * @param threads number of avialable threads
     * @param count   number of items in value list
     * @return int array of sizes
     */
    private int[] getSizes(int threads, int count) {
        int[] sizes;
        if (count < threads) {
            sizes = new int[count];
            Arrays.fill(sizes, 1);
            return sizes;
        }
        sizes = new int[threads];
        Arrays.fill(sizes, count / threads);
        int mod = count % threads;
        int index = 0;
        while (mod > 0) {
            sizes[index] += 1;
            mod--;
            index++;
        }
        return sizes;
    }


    /**
     * Returns the results of the operation depending on the passed function.
     *
     * @param threads         number of avialable threads
     * @param values          list of values
     * @param threadEvaluator function used in lists processed by threads
     * @param resEvaluator    function used to get result
     * @param step            step size
     * @param <T>             value type
     * @param <R>             return type
     * @return result of given operation by value list
     * @throws InterruptedException if any thread was interrupted
     */
    private <T, R> R getResult(int threads,
                               List<? extends T> values,
                               Function<Stream<? extends T>, R> threadEvaluator,
                               Function<Stream<? extends R>, R> resEvaluator,
                               int step) throws InterruptedException {

        List<? extends T> newValues = new StepList<>(values, step);

        int[] sizes = getSizes(threads, newValues.size());
        int usedThreadCount = sizes.length;
        List<Stream<? extends T>> sublists = new ArrayList<>();
        int leftBound = 0;
        for (int size : sizes) {
            sublists.add(newValues.subList(leftBound, leftBound + size).stream());
            leftBound += size;
        }
        if (mapper != null) {
            List<R> res = mapper.map(threadEvaluator, sublists);
            return resEvaluator.apply(res.stream());
        }
        Thread[] threadArr = new Thread[usedThreadCount];
        List<R> threadResult = new ArrayList<>(Collections.nCopies(usedThreadCount, null));
        IntStream.range(0, usedThreadCount).forEach(i -> threadArr[i] = new Thread(
                () -> threadResult
                        .set(i, threadEvaluator
                                .apply(sublists.get(i)
                                ))
        ));
        Arrays.stream(threadArr).forEach(Thread::start);
        for (Thread t : threadArr) {
            t.join();
        }
        return resEvaluator.apply(threadResult.stream());
    }

    private static class StepList<T> extends AbstractList<T> {
        private final List<T> values;
        private final int step;

        public StepList(List<T> values, int step) {
            this.values = values;
            this.step = step;
        }

        @Override
        public T get(int index) {
            return values.get(index * step);
        }

        @Override
        public int size() {
            return Math.ceilDiv(values.size(), step);
        }
    }
}
