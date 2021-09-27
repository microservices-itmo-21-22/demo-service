package com.itmo.microservices.demo.bombardier.flow

import com.itmo.microservices.demo.bombardier.flow.OrderStatus.OrderCollecting
import java.util.*


interface ServiceApi {
    suspend fun getUser(id: UUID): User
    suspend fun createUser(name: String): User

    //suspend fun userFinancialHistory(userId: UUID): List<FinancialLogRecord>
    suspend fun userFinancialHistory(userId: UUID, orderId: UUID): List<UserAccountFinancialLogRecord>

    suspend fun createOrder(userId: UUID): Order

    //suspend fun getOrders(userId: UUID): List<Order>
    suspend fun getOrder(orderId: UUID): Order

    suspend fun getAvailableItems(): List<CatalogItem>

    suspend fun putItemToOrder(
        orderId: UUID,
        itemId: UUID,
        amount: Amount
    ): Boolean // todo sukhoa consider using add instead of put

    suspend fun bookOrder(orderId: UUID): BookingDto //синхронный
    suspend fun getDeliverySlots(orderId: UUID): List<Long> // todo sukhoa in future we should get the Dto with slots. Slot has it's lifetime and should be active within it.
    suspend fun setDeliveryTime(orderId: UUID, time: Long)
    suspend fun payOrder(userId: UUID, orderId: UUID): PaymentSubmissionDto

    suspend fun abandonedCardHistory(orderId: UUID): List<AbandonedCardLogRecord>

    suspend fun getBookingHistory(bookingId: UUID): List<BookingLogRecord>
    //suspend fun getBookingHistory(orderId: UUID): List<BookingLogRecord>

    suspend fun deliveryLog(orderId: Order): DeliveryInfoRecord
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

typealias Amount = Int

class PaymentSubmissionDto(
    val timestamp: Long,
    val transactionId: UUID
)

data class User(
    val id: UUID = UUID.randomUUID(),
    val name: String
)

data class UserAccountFinancialLogRecord( // todo think of refund via TP system
    val type: FinancialOperationType,
    val amount: Amount,
    val orderId: UUID,
    val paymentTransactionId: UUID,
    val timestamp: Long = System.currentTimeMillis()
)

enum class FinancialOperationType {
    DEPOSIT,
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
    val amount: Amount, // number of items allowed for booking
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
    val amount: Amount,
    val timestamp: Long = System.currentTimeMillis(),
)

sealed class OrderStatus {
    object OrderCollecting : OrderStatus()
    object OrderDiscarded : OrderStatus()
    object OrderBooked : OrderStatus()
    class OrderPayed(val paymentTime: Long) : OrderStatus()
    class OrderInDelivery(val deliveryStartTime: Long) : OrderStatus()
    class OrderDelivered(val deliveryStartTime: Long, deliveryFinishTime: Long) : OrderStatus()
    class OrderFailed(reason: String, previousStatus: OrderStatus) : OrderStatus()
}

//val orderStateMachine = OrderStatusStateMachine(
//    listOf(
//        OrderCollecting::class to OrderBooked::class,
//        OrderCollecting::class to OrderDiscarded::class,
//        OrderBooked::class to OrderCollecting::class, // payment haven't succeeded withing given time period or booking was cancelled
//        OrderBooked::class to OrderBooked::class, // still haven't been payed but timeout haven't passed
//        OrderBooked::class to OrderPayed::class,
//    )
//)
//
//class OrderStatusStateMachine(legalTransitions: List<Pair<KClass<out OrderStatus>, KClass<out OrderStatus>>>) {
//    private val transitions = ConcurrentHashMap<KClass<out OrderStatus>, MutableSet<KClass<out OrderStatus>>>()
//
//    init {
//        legalTransitions.forEach { (from, to) ->
//            transitions.computeIfAbsent(from) { mutableSetOf() }.add(to)
//        }
//    }
//
//    fun isTransitionAllowed(from: OrderStatus, to: OrderStatus): Boolean {
//        return transitions[from::class]?.contains(to::class)
//            ?: throw IllegalStateException("No such from status : $from")
//    }
//}

data class Order(
    val id: UUID = UUID.randomUUID(),
    val timeCreated: Long = System.currentTimeMillis(),
    val status: OrderStatus = OrderCollecting,
    val itemsMap: Map<OrderItem, Amount> = emptyMap(),
    val deliveryDuration: Long? = null,
    val paymentHistory: List<PaymentLogRecord> = listOf()
)

data class AbandonedCardLogRecord(
    val transactionId: UUID,
    val timestamp: Long,
    val userInteracted: Boolean
)

class PaymentLogRecord(
    val timestamp: Long,
    val status: PaymentStatus,
    val amount: Amount,
    val transactionId: UUID,
)

enum class PaymentStatus {
    FAILED,
    FAILED_NOT_ENOUGH_MONEY, // todo sukhoa Elina, rename
    SUCCESS
}

enum class BookingStatus {
    FAILED,
    SUCCESS
}