import java.util.concurrent.atomic.*

/**
 * @author Galkin Gleb
 */
class Solution(val env: Environment) : Lock<Solution.Node> {
    private val tail = AtomicReference<Node?>(null)

    override fun lock(): Node {
        val node = Node()
        val pred = tail.getAndSet(node)
        if (pred != null) {
            pred.next.set(node)
            while (node.locked.value) {
                env.park()
            }
        }
        return node
    }

    override fun unlock(node: Node) {
        if (node.next.value == null) {
            if (tail.compareAndSet(node, null)) return
            while (node.next.value == null) {}
        }
        node.next.value.locked.set(false)
        env.unpark(node.next.value.thread)
    }

    class Node {
        val thread = Thread.currentThread() // запоминаем поток, которые создал узел
        val locked = AtomicReference(true)
        val next = AtomicReference<Node>(null)
    }
}