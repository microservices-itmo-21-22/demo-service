package com.itmo.microservices.demo.bombardier.external

import com.itmo.microservices.demo.bombardier.ServiceDescriptor
import com.itmo.microservices.demo.bombardier.external.storage.ItemStorage
import com.itmo.microservices.demo.bombardier.external.storage.OrderStorage
import com.itmo.microservices.demo.bombardier.external.storage.UserStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URL
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

//@Component
class ExternalServiceSimulator(
    private val orderStorage: OrderStorage,
    private val userStorage: UserStorage,
    private val itemStorage: ItemStorage
) : ExternalServiceApi {

    override val descriptor = ServiceDescriptor("Simulator", URL("https://youtube.com/watch?v=dQw4w9WgXcQ"))

    private val financialLog = ConcurrentHashMap<UUID, MutableList<UserAccountFinancialLogRecord>>()
    private val deliveryLog = ConcurrentHashMap<UUID, DeliveryInfoRecord>()
    private val orderToUser = ConcurrentHashMap<UUID, UUID>()

    override suspend fun createUser(name: String): User {
        return userStorage.create(User(name = name, id = UUID.randomUUID())).also {
            financialLog[it.id] = mutableListOf()
        }
    }

    override suspend fun userFinancialHistory(userId: UUID, orderId: UUID?): List<UserAccountFinancialLogRecord> {
        return financialLog[userId]?.filter { it.orderId == orderId } ?: emptyList()
    }

    override suspend fun getUser(id: UUID): User {
        TODO("Not yet implemented")
    }

    override suspend fun createOrder(userId: UUID): Order { // todo sukhoa userId not used
        return orderStorage.create(Order(id = UUID.randomUUID(), timeCreated = System.currentTimeMillis(), itemsMap = mutableMapOf(), paymentHistory = mutableListOf()))
    }

    override suspend fun getOrder(userId: UUID, orderId: UUID): Order {
        return orderStorage.get(orderId)
    }

    override suspend fun abandonedCardHistory(orderId: UUID): List<AbandonedCardLogRecord> {
        TODO("Not yet implemented")
    }

    override suspend fun getDeliverySlots(userId: UUID, number: Int): List<Duration> {
        return mutableListOf<Duration>().also {
            for (i in 0..Random.nextInt(1, 100)) {
                it.add(Duration.ofSeconds(Random.nextLong(1, 20)))
            }
        }
    }

    override suspend fun setDeliveryTime(userId: UUID, orderId: UUID, slot: Duration) {
        orderStorage.getAndUpdate(orderId) { order ->
            order.copy(deliveryDuration = slot)
        }
    }

    override suspend fun payOrder(userId: UUID, orderId: UUID): PaymentSubmissionDto {
        orderToUser[orderId] = userId
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
                        transactionId,
                        System.currentTimeMillis()
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

    override suspend fun simulateDelivery(userId: UUID, orderId: UUID) {
        orderStorage.getAndUpdate(orderId) { order ->
            order.copy(status = OrderStatus.OrderInDelivery(System.currentTimeMillis()))
        }
        val orderBeforeDelivery = getOrder(userId, orderId)
        CoroutineScope(Dispatchers.Default).launch {
            delay(Random.nextLong(orderBeforeDelivery.deliveryDuration!!.toMillis() + 1_000))
            chooseDeliveryResult(userId, orderId)
        }
    }

    private suspend fun chooseDeliveryResult(userId: UUID, orderId: UUID) {
        val order = getOrder(userId, orderId)
        val expectedDeliveryTime = Duration.ofSeconds(order.deliveryDuration!!.toMillis())
            .plus(Duration.ofMillis(order.paymentHistory.last().timestamp))
        if (System.currentTimeMillis() < expectedDeliveryTime.toMillis()) {
            orderStorage.getAndUpdate(orderId) {
                val deliveryStart = (it.status as OrderStatus.OrderInDelivery).deliveryStartTime
                it.copy(
                    status = OrderStatus.OrderDelivered(
                        deliveryStart,
                        System.currentTimeMillis()
                    )
                )
            }
            deliveryLog[orderId] = DeliveryInfoRecord(
                outcome = DeliverySubmissionOutcome.SUCCESS,
                preparedTime = 0,
                attempts = 1,
                submittedTime = (orderStorage.get(orderId).status as OrderStatus.OrderDelivered).deliveryFinishTime,
                submissionStartedTime = (orderStorage.get(orderId).status as OrderStatus.OrderDelivered).deliveryStartTime,
                transactionId = UUID.randomUUID()
            )
        } else {
            orderStorage.getAndUpdate(orderId) {
                it.copy(status = OrderStatus.OrderRefund)
            }
            order.paymentHistory.last().transactionId
            if (Random.nextInt(1, 100) < 70) {
                val userId = orderToUser[orderId]!!
                val currentLog = userFinancialHistory(userId, orderId)
                financialLog[userId]!!.add(
                    UserAccountFinancialLogRecord(
                        type = FinancialOperationType.REFUND,
                        amount = currentLog.sumOf {
                            if (it.type == FinancialOperationType.WITHDRAW) {
                                it.amount
                            } else {
                                -it.amount
                            }
                        },
                        orderId = orderId,
                        paymentTransactionId = UUID.randomUUID(),
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }

        orderToUser.remove(orderId)
    }

    /**
     * Статус заказа меняется в зависимости от результата бронирования.
     * Возвращается информация о неудачных айтемах
     */
    override suspend fun bookOrder(userId: UUID, orderId: UUID): BookingDto {
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

    private suspend fun bookItems(items: Map<OrderItem, Int>): BookingDto {
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

    override suspend fun getItems(userId: UUID, available: Boolean): List<CatalogItem> {
        return itemStorage.items.values.map { it.first }.toList()
    }

    override suspend fun putItemToOrder(userId: UUID, orderId: UUID, itemId: UUID, amount: Int): Boolean {
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

    override suspend fun getBookingHistory(userId: UUID, bookingId: UUID): List<BookingLogRecord> {
        return itemStorage.getBookingRecordsById(bookingId)
    }

    override suspend fun deliveryLog(userId: UUID, orderId: UUID): DeliveryInfoRecord {
        return deliveryLog[orderId]!!
    }
}