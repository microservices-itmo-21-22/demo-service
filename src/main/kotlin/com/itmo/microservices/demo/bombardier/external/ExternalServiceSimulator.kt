package com.itmo.microservices.demo.bombardier.external

import com.itmo.microservices.demo.bombardier.external.storage.ItemStorage
import com.itmo.microservices.demo.bombardier.external.storage.OrderStorage
import com.itmo.microservices.demo.bombardier.external.storage.UserStorage
import com.itmo.microservices.demo.bombardier.flow.*
import kotlinx.coroutines.delay
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

    override suspend fun getBucketAliveLogRecord(id: UUID): List<BucketLogRecord> {
        TODO("Not yet implemented")
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

    /**
     * Статус заказа меняется в зависимости от результата бронирования.
     * Возвращается информация о неудачных айтемах
     */
    override suspend fun finalizeOrder(orderId: UUID): BookingDto {
        return bookItems(orderStorage.get(orderId).itemsMap).also { bookingResult ->
            orderStorage.getAndUpdate(orderId) { existing ->
                val newOrderStatus = if (bookingResult.failedItems.isEmpty()) {
                    OrderStatus.OrderBooked
                } else {
                    OrderStatus.OrderCollecting
                }
                existing.copy(status = newOrderStatus)
            }
        }
    }

    private suspend fun bookItems(items: Map<Item, Amount>): BookingDto {
        val bookingId = UUID.randomUUID()

        val successfullyBooked = mutableSetOf<UUID>()
        val failedToBook = mutableSetOf<UUID>()

        for ((item, amount) in items) {
            itemStorage.getAndUpdate(itemId = item.id) { existingItem ->
                val bookingStatus =
                    if (existingItem.amount - amount > 0) BookingStatus.SUCCESS else BookingStatus.FAILED

                when (bookingStatus) {
                    BookingStatus.SUCCESS -> {
                        itemStorage.bookingRecords.add(BookingLogRecord(bookingId, item.id, bookingStatus, amount))
                        successfullyBooked.add(item.id)
                        existingItem.copy(amount = existingItem.amount - amount)
                    }
                    BookingStatus.FAILED -> {
                        itemStorage.bookingRecords.add(BookingLogRecord(bookingId, item.id, bookingStatus, amount))
                        failedToBook.add(item.id)
                        existingItem
                    }
                }
            }
        }

        // rollback in case booking failed
        if (failedToBook.isNotEmpty()) {
            items
                .filter { it.key.id in successfullyBooked }
                .forEach { (item, refundAmount) ->
                    itemStorage.getAndUpdate(item.id) { existing ->
                        existing.copy(amount = existing.amount + refundAmount)
                    }
                }

        }

        return if (failedToBook.isNotEmpty())
            BookingDto(bookingId, failedItems = failedToBook.toSet())
        else
            BookingDto(bookingId)
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

    override suspend fun getBookingHistory(bookingId: UUID): List<BookingLogRecord> {
        return itemStorage.getBookingRecordsById(bookingId)
    }
}

