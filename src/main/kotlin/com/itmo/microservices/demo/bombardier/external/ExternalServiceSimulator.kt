package com.itmo.microservices.demo.bombardier.external

import com.itmo.microservices.demo.bombardier.external.storage.ItemStorage
import com.itmo.microservices.demo.bombardier.external.storage.OrderStorage
import com.itmo.microservices.demo.bombardier.external.storage.UserStorage
import com.itmo.microservices.demo.bombardier.flow.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.random.Random

class ExternalServiceSimulator(
    private val orderStorage: OrderStorage,
    private val userStorage: UserStorage,
    private val itemStorage: ItemStorage
) : ServiceApi {
    companion object {
        val log = LoggerFactory.getLogger(ExternalServiceSimulator::class.java)
    }

    private val financialLog = ConcurrentHashMap<UUID, MutableList<UserAccountFinancialLogRecord>>()

    override suspend fun createUser(name: String): User {
        return userStorage.create(User(name = name)).also {
            financialLog[it.id] = mutableListOf()
        }
    }

    override suspend fun userFinancialHistory(userId: UUID, orderId: UUID): List<UserAccountFinancialLogRecord> {
        return financialLog[userId] ?: emptyList()
    }

    override suspend fun getUser(id: UUID): User {
        TODO("Not yet implemented")
    }

    override suspend fun createOrder(userId: UUID): Order { // todo sukhoa userId not used
        return orderStorage.create(Order())
    }

    override suspend fun getOrder(orderId: UUID): Order {
        return orderStorage.get(orderId)
    }

    override suspend fun abandonedCardHistory(orderId: UUID): List<AbandonedCardLogRecord> {
        TODO("Not yet implemented")
    }

    override suspend fun getDeliverySlots(orderId: UUID): List<Duration> {
        return mutableListOf<Duration>().also {
            for (i in 0..Random.nextInt(100)) {
                it.add(Duration.ofSeconds(Random.nextLong(20)))
            }
        }
    }

    override suspend fun setDeliveryTime(orderId: UUID, time: Duration) {
        orderStorage.getAndUpdate(orderId) { order ->
            order.copy(deliveryDuration = time)
        }
    }

    override suspend fun payOrder(userId: UUID, orderId: UUID): PaymentSubmissionDto {
        delay(Random.nextLong(1_000))

        val submissionTime = System.currentTimeMillis()
        val transactionId = UUID.randomUUID()

        // async payment processing
        CoroutineScope(Dispatchers.Default).launch {
            delay(Random.nextLong(7_000))
            val paymentStatus = if (Random.nextBoolean()) {
                PaymentLogRecord(
                    System.currentTimeMillis(),
                    PaymentStatus.FAILED,
                    100,
                    transactionId = transactionId
                ) // todo sukhoa Elina change hardcoded stuff
            } else {
                financialLog[userId]!!.add(
                    UserAccountFinancialLogRecord(
                        FinancialOperationType.WITHDRAW,
                        100,
                        orderId,
                        transactionId
                    )
                )
                PaymentLogRecord(System.currentTimeMillis(), PaymentStatus.SUCCESS, 100, transactionId = transactionId)
            }

            orderStorage.getAndUpdate(orderId = orderId) { order ->
                order.copy(
                    status = if (paymentStatus.status == PaymentStatus.SUCCESS)
                        OrderStatus.OrderPayed(paymentTime = paymentStatus.timestamp)
                    else
                        order.status,
                    paymentHistory = order.paymentHistory + listOf(paymentStatus)
                )
            }
        }

        return PaymentSubmissionDto(submissionTime, transactionId)
    }

    /**
     * Статус заказа меняется в зависимости от результата бронирования.
     * Возвращается информация о неудачных айтемах
     */
    override suspend fun bookOrder(orderId: UUID): BookingDto {
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

    private suspend fun bookItems(items: Map<OrderItem, Amount>): BookingDto {
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

    override suspend fun getAvailableItems(): List<CatalogItem> {
        return itemStorage.items.values.map { it.first }.toList()
    }

    override suspend fun putItemToOrder(orderId: UUID, itemId: UUID, amount: Amount): Boolean {
        orderStorage.getAndUpdate(orderId = orderId) { order ->
            val item = itemStorage.get(itemId)
            order.copy(
                itemsMap = order.itemsMap.filterKeys { it.id != itemId }
                        + (OrderItem(id = item.id, title = item.title, price = item.price) to amount),
                status = if (order.status == OrderStatus.OrderBooked) OrderStatus.OrderCollecting else order.status
            )
        }
        return true
    }

    override suspend fun getBookingHistory(bookingId: UUID): List<BookingLogRecord> {
        return itemStorage.getBookingRecordsById(bookingId)
    }

    override suspend fun deliveryLog(orderId: Order): DeliveryInfoRecord {
        TODO("Not yet implemented")
    }
}