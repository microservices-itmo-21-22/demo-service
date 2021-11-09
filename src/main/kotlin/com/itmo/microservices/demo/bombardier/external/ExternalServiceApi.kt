package com.itmo.microservices.demo.bombardier.external

import com.itmo.microservices.demo.bombardier.external.OrderStatus.OrderCollecting
import java.time.Duration
import java.util.*


interface ExternalServiceApi {
    suspend fun getUser(id: UUID): User
    suspend fun createUser(name: String): User

    suspend fun userFinancialHistory(userId: UUID) = userFinancialHistory(userId, null)
    suspend fun userFinancialHistory(userId: UUID, orderId: UUID?): List<UserAccountFinancialLogRecord>

    suspend fun createOrder(userId: UUID): Order

    //suspend fun getOrders(userId: UUID): List<Order>
    suspend fun getOrder(userId: UUID, orderId: UUID): Order

    suspend fun getItems(userId: UUID, available: Boolean): List<CatalogItem>
    suspend fun getAvailableItems(userId: UUID) = getItems(userId, true)

    suspend fun putItemToOrder(
        userId: UUID,
        orderId: UUID,
        itemId: UUID,
        amount: Int
    ): Boolean // todo sukhoa consider using add instead of put

    suspend fun bookOrder(userId: UUID, orderId: UUID): BookingDto //синхронный
    suspend fun getDeliverySlots(userId: UUID, number: Int): List<Duration> // todo sukhoa in future we should get the Dto with slots. Slot has it's lifetime and should be active within it.
    suspend fun setDeliveryTime(userId: UUID, orderId: UUID, slot: Duration): BookingDto
    suspend fun payOrder(userId: UUID, orderId: UUID): PaymentSubmissionDto

    suspend fun simulateDelivery(userId: UUID, orderId: UUID)

    suspend fun abandonedCardHistory(orderId: UUID): List<AbandonedCardLogRecord>

    suspend fun getBookingHistory(bookingId: UUID): List<BookingLogRecord>
    //suspend fun getBookingHistory(orderId: UUID): List<BookingLogRecord>

    suspend fun deliveryLog(orderId: UUID): DeliveryInfoRecord
}

class DeliveryInfoRecord(
    val outcome: DeliverySubmissionOutcome,
    val preparedTime: Long,
    val attempts: Int = 0,
    val submittedTime: Long? = null,
    val transactionId: UUID?,
    val submissionStartedTime: Long? = null,
)

enum class DeliverySubmissionOutcome {
    SUCCESS,
    FAILURE,
    EXPIRED
}


class PaymentSubmissionDto(
    val timestamp: Long,
    val transactionId: UUID
)

data class User(
    val id: UUID,
    val username: String,
    val name: String
)

data class UserAccountFinancialLogRecord(
    val type: FinancialOperationType,
    val amount: Int,
    val orderId: UUID,
    val paymentTransactionId: UUID,
    val timestamp: Long
)

enum class FinancialOperationType {
    REFUND,
    WITHDRAW
}

data class BookingDto(
    val id: UUID,
    val failedItems: Set<UUID> = emptySet()
)

data class CatalogItem(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String = "There should bw the desc",
    val price: Int = 100,
    val amount: Int, // number of items allowed for booking
)

data class OrderItem(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val price: Int = 100
)

class BookingLogRecord(
    val bookingId: UUID,
    val itemId: UUID,
    val status: BookingStatus,
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis(),
)

sealed class OrderStatus {
    object OrderCollecting : OrderStatus()
    object OrderDiscarded : OrderStatus()
    object OrderBooked : OrderStatus()
    object OrderRefund: OrderStatus()
    class OrderPayed(val paymentTime: Long) : OrderStatus()
    class OrderInDelivery(val deliveryStartTime: Long) : OrderStatus()
    class OrderDelivered(val deliveryStartTime: Long, val deliveryFinishTime: Long) : OrderStatus()
    class OrderFailed(reason: String, previousStatus: OrderStatus) : OrderStatus()
}

data class Order(
    val id: UUID,
    val timeCreated: Long,
    val status: OrderStatus = OrderCollecting,
    val itemsMap: Map<OrderItem, Int>,
    val deliveryDuration: Duration? = null,
    val paymentHistory: List<PaymentLogRecord>
)

data class AbandonedCardLogRecord(
    val transactionId: UUID,
    val timestamp: Long,
    val userInteracted: Boolean
)

class PaymentLogRecord(
    val timestamp: Long,
    val status: PaymentStatus,
    val amount: Int,
    val transactionId: UUID,
)

enum class PaymentStatus {
    FAILED,
    SUCCESS
}

enum class BookingStatus {
    FAILED,
    SUCCESS
}