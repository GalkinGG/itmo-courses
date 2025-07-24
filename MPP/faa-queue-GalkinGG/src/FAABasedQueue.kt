import java.util.concurrent.atomic.*

/**
 * @author Galkin Gleb
 */
class FAABasedQueue<E> : Queue<E> {

    private val head: AtomicReference<Segment>
    private val tail: AtomicReference<Segment>
    private val enqIdx = AtomicLong(0)
    private val deqIdx = AtomicLong(0)

    init {
        val dummy = Segment(0)
        head = AtomicReference(dummy)
        tail = AtomicReference(dummy)
    }

    override fun enqueue(element: E) {
        while (true) {
            val curTail = tail.get()
            val i = enqIdx.getAndIncrement()
            val s = findSegment(curTail, i / SEGMENT_SIZE)
            moveTailForward(curTail, s)
            if (s.cells.compareAndSet((i % SEGMENT_SIZE).toInt(), null, element)) return
        }
    }

    override fun dequeue(): E? {
        while (true) {
            if (deqIdx.get() >= enqIdx.get()) return null
            val curHead = head.get()
            val i = deqIdx.getAndIncrement()
            val s = findSegment(curHead, i / SEGMENT_SIZE)
            moveHeadForward(curHead, s)
            if (s.cells.compareAndSet((i % SEGMENT_SIZE).toInt(), null, POISONED)) continue
            return s.cells.get((i % SEGMENT_SIZE).toInt()) as E
        }
    }

    private fun findSegment(
        p: Segment,
        i: Long
    ): Segment {
        var currSegment = p
        while (currSegment.id != i) {
            currSegment.next.compareAndSet(null, Segment(currSegment.id + 1))
            currSegment = currSegment.next.get()!!
        }
        return currSegment
    }

    private fun moveTailForward(s: Segment, e: Segment) {
        if (s.id < e.id) {
            tail.compareAndSet(s, e)
        }
    }

    private fun moveHeadForward(s: Segment, e: Segment) {
        if (s.id < e.id) {
            head.compareAndSet(s, e)
        }
    }
}

private class Segment(val id: Long) {
    val next = AtomicReference<Segment?>(null)
    val cells = AtomicReferenceArray<Any?>(SEGMENT_SIZE)
}

// DO NOT CHANGE THIS CONSTANT
private const val SEGMENT_SIZE = 2

private val POISONED = Any()
