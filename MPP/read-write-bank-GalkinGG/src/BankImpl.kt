import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

/**
 * Bank implementation.
 *
 *
 *
 * @author Galkin Gleb
 */
class BankImpl(n: Int) : Bank {
    private val accounts: Array<Account> = Array(n) { Account() }

    override val numberOfAccounts: Int
        get() = accounts.size

    override fun getAmount(index: Int): Long {
        val account = accounts[index]
        val lock = account.lock
        return lock.readLock().withLock { account.amount }
    }

    override val totalAmount: Long
        get() {
            accounts.forEach { account -> account.lock.readLock().lock() }
            val res = accounts.sumOf { account ->
                account.amount
            }
            accounts.reversed().forEach { account -> account.lock.readLock().unlock() }
            return res
        }

    override fun deposit(index: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        val account = accounts[index]
        val lock = account.lock
        return lock.writeLock().withLock {
            check(amount <= Bank.MAX_AMOUNT && account.amount + amount <= Bank.MAX_AMOUNT) { "Overflow" }
            account.amount += amount
            account.amount
        }
    }

    override fun withdraw(index: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        val account = accounts[index]
        val lock = account.lock
        return lock.writeLock().withLock {
            check(account.amount - amount >= 0) { "Underflow" }
            account.amount -= amount
            account.amount
        }
    }

    override fun transfer(fromIndex: Int, toIndex: Int, amount: Long) {
        require(amount > 0) { "Invalid amount: $amount" }
        require(fromIndex != toIndex) { "fromIndex == toIndex" }
        val from = accounts[fromIndex]
        val to = accounts[toIndex]
        var firstLock = from.lock
        var secondLock = to.lock
        if (fromIndex >= toIndex) {
            firstLock = to.lock
            secondLock = from.lock
        }
        try {
            firstLock.writeLock().lock()
            secondLock.writeLock().lock()
            check(amount <= from.amount) { "Underflow" }
            check(amount <= Bank.MAX_AMOUNT && to.amount + amount <= Bank.MAX_AMOUNT) { "Overflow" }
            from.amount -= amount
            to.amount += amount
        } finally {
            secondLock.writeLock().unlock()
            firstLock.writeLock().unlock()
        }
    }

    override fun consolidate(fromIndices: List<Int>, toIndex: Int) {
        require(fromIndices.isNotEmpty()) { "empty fromIndices" }
        require(fromIndices.distinct() == fromIndices) { "duplicates in fromIndices" }
        require(toIndex !in fromIndices) { "toIndex in fromIndices" }
        val fromList = fromIndices.map { accounts[it] }
        val to = accounts[toIndex]
        val lockList = (fromIndices + toIndex).sorted().map { accounts[it].lock }
        try {
            lockList.forEach { it.writeLock().lock() }
            val amount = fromList.sumOf { it.amount }
            check(to.amount + amount <= Bank.MAX_AMOUNT) { "Overflow" }
            for (from in fromList) from.amount = 0
            to.amount += amount
        } finally {
            lockList.reversed().forEach { it.writeLock().unlock() }
        }
    }

    /**
     * Private account data structure.
     */
    class Account {
        /**
         * Amount of funds in this account.
         */
        var amount: Long = 0

        val lock = ReentrantReadWriteLock()
    }
}