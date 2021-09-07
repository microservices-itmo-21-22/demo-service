package com.itmo.microservices.demo.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class ExternalServiceSimulator(
        private val orderStorage: OrderStorage,
        private val userStorage: UserStorage,
        private val itemStorage: ItemStorage
) : ServiceApi {
    companion object {
        val log = LoggerFactory.getLogger(ExternalServiceSimulator::class.java)
    }

    private val financialLog = ConcurrentHashMap<UUID, MutableList<FinancialLogRecord>>()

    override suspend fun createUser(name: String, accountAmount: Int): User {
        return userStorage.create(User(name = name, accountAmount = accountAmount)).also {
            financialLog[it.id] = mutableListOf()
        }
    }

    override suspend fun getFinancialHistory(userId: UUID, orderId: UUID): List<FinancialLogRecord> {
        return financialLog[userId] ?: emptyList()
    }

    override suspend fun getUser(id: UUID): User {
        TODO("Not yet implemented")
    }

    override suspend fun createOrder(userId: UUID): Order { // todo sukhoa userId not used
        return orderStorage.create(Order())
    }

    override suspend fun getOrder(id: UUID): Order {
        return orderStorage.get(id)
    }

    override suspend fun getDeliverySlots(id: UUID): List<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun setDeliveryTime(id: UUID, time: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun payOrder(userId: UUID, orderId: UUID): Order {
        delay(Random.nextLong(3_000))

        val updated = orderStorage.getAndUpdate(orderId = orderId) { order ->
            val paymentStatus = if (Random.nextBoolean()) {
                PaymentLogRecord(
                        System.currentTimeMillis(),
                        PaymentStatus.FAILED,
                        100
                ) // todo sukhoa Elina change hardcoded stuff
            } else {
                financialLog[userId]!!.add(FinancialLogRecord(FinancialOperationType.WITHDRAW, 100, orderId))
                userStorage.getAndUpdate(userId) { user ->
                    user.copy(accountAmount = user.accountAmount - 100)
                }
                PaymentLogRecord(System.currentTimeMillis(), PaymentStatus.SUCCESS, 100)
            }

            order.copy(
                    status = if (paymentStatus.status == PaymentStatus.SUCCESS)
                        OrderStatus.OrderPayed(paymentTime = paymentStatus.timestamp)
                    else
                        order.status,
                    paymentHistory = order.paymentHistory + listOf(paymentStatus)
            )
        }

        return updated
    }

    override suspend fun finalizeOrder(orderId: UUID): BookingDto {
        TODO("Not yet implemented")
    }

    override suspend fun getItems(): List<Item> {
        return itemStorage.items.values.map { it.first }.toList()
    }

    override suspend fun addItem(orderId: UUID, itemId: UUID, amount: Amount): Boolean {
        orderStorage.getAndUpdate(orderId = orderId) { order ->
            val item = itemStorage.get(itemId)
            order.copy(
                    itemsMap = order.itemsMap.filterKeys { it.id != itemId }
                            + (item.copy(amount = amount) to amount)
            )
        }
        return true
    }
}

class OrderStorage {
    private val orders = ConcurrentHashMap<UUID, Pair<Order, Mutex>>()

    suspend fun create(order: Order): Order {
        val existing = orders.putIfAbsent(order.id, order to Mutex())
        if (existing != null) {
            throw IllegalArgumentException("Order already exists: $order")
        }
        return order
    }

    suspend fun getAndUpdate(orderId: UUID, updateFunction: suspend (Order) -> Order): Order {
        val (_, mutex) = orders[orderId] ?: throw IllegalArgumentException("No such order: $orderId")
        mutex.withLock {
            val (order, _) = orders[orderId] ?: throw IllegalArgumentException("No such order: $orderId")
            val updatedOrder = updateFunction(order)
            orders[orderId] = updatedOrder to mutex
            return updatedOrder
        }
    }

    suspend fun get(orderId: UUID) = orders[orderId]?.first ?: throw IllegalArgumentException("No such order: $orderId")
}

class ItemStorage {
    val items: ConcurrentHashMap<UUID, Pair<Item, Mutex>> = listOf(
            Item(title = "Socks", amount = Int.MAX_VALUE),
            Item(title = "Book", amount = Int.MAX_VALUE),
            Item(title = "Plate", amount = Int.MAX_VALUE),
            Item(title = "Table", amount = Int.MAX_VALUE),
            Item(title = "Chair", amount = Int.MAX_VALUE),
            Item(title = "Watch", amount = Int.MAX_VALUE),
            Item(title = "Bed", amount = Int.MAX_VALUE)
    ).map { it.id to (it to Mutex()) }.toMap(ConcurrentHashMap<UUID, Pair<Item, Mutex>>())

    suspend fun create(item: Item): Item {
        val existing = items.putIfAbsent(item.id, item to Mutex())
        if (existing != null) {
            throw IllegalArgumentException("Item already exists: $item")
        }
        return item
    }

    suspend fun getAndUpdate(itemId: UUID, updateFunction: suspend (Item) -> Item): Item {
        val (_, mutex) = items[itemId] ?: throw IllegalArgumentException("No such item: $itemId")
        mutex.withLock {
            val (item, _) = items[itemId] ?: throw IllegalArgumentException("No such item: $itemId")
            val updatedItem = updateFunction(item)
            items[itemId] = updatedItem to mutex
            return updatedItem
        }
    }

    suspend fun get(itemId: UUID) = items[itemId]?.first ?: throw IllegalArgumentException("No such item: $itemId")
}

class UserStorage {
    private val users = ConcurrentHashMap<UUID, Pair<User, Mutex>>()

    suspend fun create(user: User): User {
        val existing = users.putIfAbsent(user.id, user to Mutex())
        if (existing != null) {
            throw IllegalArgumentException("User already exists: $user")
        }
        return user
    }

    suspend fun getAndUpdate(userId: UUID, updateFunction: suspend (User) -> User): User {
        val (_, mutex) = users[userId] ?: throw IllegalArgumentException("No such user: $userId")
        mutex.withLock {
            val (order, _) = users[userId] ?: throw IllegalArgumentException("No such user: $userId")
            val updatedOrder = updateFunction(order)
            users[userId] = updatedOrder to mutex
            return updatedOrder
        }
    }

    suspend fun get(userId: UUID) = users[userId]?.first ?: throw IllegalArgumentException("No such user: $userId")
}