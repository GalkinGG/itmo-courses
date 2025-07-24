import java.util.concurrent.*
import java.util.concurrent.atomic.*

/**
 * @author Galkin Gleb
 */
class FlatCombiningQueue<E> : Queue<E> {
    private val queue = ArrayDeque<E>()
    private val combinerLock = AtomicBoolean(false)
    private val tasksForCombiner = AtomicReferenceArray<Any?>(TASKS_FOR_COMBINER_SIZE)

    override fun enqueue(element: E) {
        var ind = -1
        var inCombiner = false
        while (true) {
            if (tryLock()) {
                helpOthers()
                if (inCombiner) {
                    tasksForCombiner.set(ind, null)
                } else {
                    queue.addLast(element)
                }
                unlock()
                return
            }
            if (inCombiner) {
                val res = tasksForCombiner.get(ind)
                if (res is Result<*>) {
                    tasksForCombiner.set(ind, null)
                    return
                }
            } else {
                ind = randomCellIndex()
                if (tasksForCombiner.compareAndSet(ind, null, element)) {
                    inCombiner = true
                }
            }
        }
    }

    private fun helpOthers() {
        for (i in 0..<tasksForCombiner.length()) {
            val task = tasksForCombiner.get(i)
            if (task == Dequeue) {
                tasksForCombiner.set(i, Result(queue.removeFirstOrNull()))
            } else if (task !is Result<*> && task as? E != null) {
                tasksForCombiner.set(i, Result(queue.addLast(task as E)))
            }
        }
    }

    override fun dequeue(): E? {
        var ind = -1
        var inCombiner = false
        while (true) {
            if (tryLock()) {
                helpOthers()
                val res = if (inCombiner) {
                    (tasksForCombiner.getAndSet(ind, null) as Result<*>).value as E
                } else {
                    queue.removeFirstOrNull()
                }
                unlock()
                return res
            }
            if (inCombiner) {
                val res = tasksForCombiner.get(ind)
                if (res is Result<*>) {
                    tasksForCombiner.set(ind, null)
                    return res.value as E
                }
            } else {
                ind = randomCellIndex()
                if (tasksForCombiner.compareAndSet(ind, null, Dequeue)) {
                    inCombiner = true
                }
            }
        }
    }

    private fun tryLock(): Boolean {
        return combinerLock.compareAndSet(false, true)
    }

    private fun unlock() {
        combinerLock.set(false)
    }

    private fun randomCellIndex(): Int =
        ThreadLocalRandom.current().nextInt(tasksForCombiner.length())
}

private const val TASKS_FOR_COMBINER_SIZE = 3 // Do not change this constant!


private object Dequeue

private class Result<V>(
    val value: V
)