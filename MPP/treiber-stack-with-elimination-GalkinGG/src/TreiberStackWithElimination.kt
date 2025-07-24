import java.util.concurrent.*
import java.util.concurrent.atomic.*

/**
 * @author Galkin Gleb
 */
open class TreiberStackWithElimination<E> : Stack<E> {
    private val stack = TreiberStack<E>()

    private val eliminationArray = AtomicReferenceArray<Any?>(ELIMINATION_ARRAY_SIZE)

    override fun push(element: E) {
        if (tryPushElimination(element)) return
        stack.push(element)
    }

    protected open fun tryPushElimination(element: E): Boolean {
        val index = randomCellIndex()
        if (eliminationArray.compareAndSet(index, CELL_STATE_EMPTY, element)) {
            for (i in 0..ELIMINATION_WAIT_CYCLES) {}
            return eliminationArray.getAndSet(index, CELL_STATE_EMPTY) == CELL_STATE_RETRIEVED
        }
        return false
    }

    override fun pop(): E? = tryPopElimination() ?: stack.pop()

    private fun tryPopElimination(): E? {
        val index = randomCellIndex()
        val elem = eliminationArray.get(index)
        if (elem != CELL_STATE_EMPTY && elem != CELL_STATE_RETRIEVED) {
            return if (eliminationArray.compareAndSet(index, elem, CELL_STATE_RETRIEVED)) {
                elem as E?
            } else {
                null
            }
        }
        return null
    }

    private fun randomCellIndex(): Int =
        ThreadLocalRandom.current().nextInt(eliminationArray.length())

    companion object {
        private const val ELIMINATION_ARRAY_SIZE = 2 // Do not change!
        private const val ELIMINATION_WAIT_CYCLES = 1 // Do not change!

        // Initially, all cells are in EMPTY state.
        private val CELL_STATE_EMPTY = null

        // `tryPopElimination()` moves the cell state
        // to `RETRIEVED` if the cell contains element.
        private val CELL_STATE_RETRIEVED = Any()
    }
}
