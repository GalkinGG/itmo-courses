package info.kgeorgiy.ja.galkin.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {

    private final List<T> set;

    private final Comparator<? super T> comparator;

    private Boolean hasNull = false;

    public ArraySet(Collection<? extends T> c) {
        this(c, null);
    }

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    private ArraySet(ArraySet<T> s, int fromIndex, int toIndex) {
        this.set = s.set.subList(fromIndex, toIndex);
        this.comparator = s.comparator;
    }

    public ArraySet(Collection<? extends T> c, Comparator<? super T> comparator) {
        TreeSet<T> treeSet = new TreeSet<>(comparator);
        treeSet.addAll(c);
        set = List.copyOf(treeSet);
        this.comparator = comparator;
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    private int getIndexOf(T element) {
        int index = Collections.binarySearch(set, element, comparator);
        return index < 0 ? Math.abs(index) - 1 : index;
    }

    private ArraySet<T> getSubSet(T fromElement, T toElement) {
        if (toElement == null && !hasNull) {
            int index = getIndexOf(fromElement);
            return new ArraySet<>(this, index, size());
        }
        if (fromElement == null && !hasNull) {
            int index = getIndexOf(toElement);
            return new ArraySet<>(this, 0, index);
        }
        int fromIndex = getIndexOf(fromElement);
        int toIndex = getIndexOf(toElement);
        return new ArraySet<>(this, fromIndex, toIndex);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if ((fromElement == null || toElement == null) && !contains(null)) {
            throw new NullPointerException("Null value not allowed");
        }
        if (comparator != null && comparator.compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromElement must be less or equal to toElement");
        }
        if (fromElement == null || toElement == null) {
            throw new NullPointerException("Null value not allowed with null comparator");
        }
        if (comparator == null && ((Comparable<T>) fromElement).compareTo(toElement) > 0) {
            throw new IllegalArgumentException("fromElement must be less or equal to toElement");
        }
        return getSubSet(fromElement, toElement);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        if (toElement == null && !contains(null)) {
            throw new NullPointerException("Null value not allowed");
        }
        return getSubSet(null, toElement);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        if (fromElement == null && !contains(null)) {
            throw new NullPointerException("Null value not allowed");
        }
        return getSubSet(fromElement, null);
    }

    private void isSetEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("Set is empty");
        }
    }

    @Override
    public T first() {
        isSetEmpty();
        return set.get(0);
    }

    @Override
    public T last() {
        isSetEmpty();
        return set.get(size() - 1);
    }

    @Override
    public int size() {
        return set.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(set, (T) o, comparator) >= 0;
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }
}
