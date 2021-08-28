package com.itmo.microservices.demo.test

import com.itmo.microservices.demo.test.FinancialOperationType.WITHDRAW
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class ExternalServiceSimulator(
    private val orderStorage: OrderStorage,
    private val userStorage: UserStorage
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
//                financialLog[userId]!!.add(FinancialLogRecord(WITHDRAW, 100, orderId))
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

    override suspend fun getItems(): List<Item> {
        TODO("Not yet implemented")
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