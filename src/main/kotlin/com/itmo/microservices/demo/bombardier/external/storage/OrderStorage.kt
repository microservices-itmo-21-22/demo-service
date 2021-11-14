package com.itmo.microservices.demo.bombardier.external.storage

import com.itmo.microservices.demo.bombardier.external.Order
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
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