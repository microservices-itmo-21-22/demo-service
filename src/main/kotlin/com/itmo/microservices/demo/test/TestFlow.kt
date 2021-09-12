package com.itmo.microservices.demo.test

import com.itmo.microservices.demo.test.OrderStatus.OrderCollecting
import com.itmo.microservices.demo.test.OrderStatus.OrderDiscarded
import com.itmo.microservices.demo.test.PaymentStatus.FAILED
import com.itmo.microservices.demo.test.PaymentStatus.SUCCESS
import com.itmo.microservices.demo.test.TestContinuationType.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

class TestFlow(
    private val userManagement: UserManagement,
    private val serviceApi: ServiceApi
) {
    companion object {
        val log = LoggerFactory.getLogger(TestFlow::class.java)
    }

    val executor = Executors.newFixedThreadPool(4)

    val coroutineScope = CoroutineScope(executor.asCoroutineDispatcher())

    val testStages = listOf(
        ChoosingUserAccountStage(userManagement).asErrorFree(),
        OrderCreationStage(serviceApi).asErrorFree(),
        OrderCollectingStage(serviceApi).asErrorFree(), //бросание корзины, финализирование заказа (c возвратом всех зафейленных items). Если какой-то не получилось, то ничего не бронируем доставка
        OrderFinalizingStage(serviceApi).asErrorFree(),
        OrderPaymentStage(serviceApi).asRetryable().asErrorFree()
    )

    fun startTestingForService(params: TestParameters) = coroutineScope.launch {
        userManagement.createUsersPool(params.serviceName, params.numberOfUsers)
        repeat(params.parallelProcessesNumber) {
            log.info("Launch coroutine for ${params.serviceName}")
            launchNewTestFlow(params.serviceName)
        }
    }

    private fun launchNewTestFlow(serviceName: String) {
        coroutineScope.launch(SupervisorJob() + TestContext(serviceName = serviceName)) {
            testStages.forEach { stage ->
                when (stage.run()) {
                    CONTINUE -> Unit
                    else -> return@launch
                }
            }
        }.invokeOnCompletion { th ->
            if (th != null) {
                log.error("Unexpected fail in test", th)
            }
            launchNewTestFlow(serviceName)
        }
    }
}

object TestCtxKey : CoroutineContext.Key<TestContext>

data class TestContext(
    val testId: UUID = UUID.randomUUID(),
    val serviceName: String,
    var userId: UUID? = null,
    var orderId: UUID? = null,
    var paymentDetails: PaymentDetails = PaymentDetails()
) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<TestContext>
        get() = TestCtxKey
}

data class PaymentDetails(
    var startedAt: Long? = null,
    var failedAt: Long? = null,
    var finishedAt: Long? = null,
    var attempt: Int = 0,
    var amount: Amount? = null,
)

enum class TestContinuationType {
    CONTINUE,
    FAIL,
    ERROR,
    RETRY,
    STOP
}

interface TestStage {
    suspend fun run(): TestContinuationType
    suspend fun testCtx() = coroutineContext[TestCtxKey]!!

    class RetryableTestStage(private val wrapped: TestStage) : TestStage {
        override suspend fun run(): TestContinuationType {
            repeat(5) {
                when (val state = wrapped.run()) {
                    CONTINUE -> return state
                    FAIL -> return state
                    RETRY -> Unit
                }
            }
            return RETRY
        }
    }

    class ExceptionFreeTestStage(override val wrapped: TestStage) : TestStage, DecoratingStage {
        override suspend fun run() = try {
            wrapped.run()
        } catch (th: Throwable) {
            var decoratedStage = wrapped
            while (decoratedStage is DecoratingStage) {
                decoratedStage = decoratedStage.wrapped
            }
            ChoosingUserAccountStage.log.error("Unexpected in ${wrapped::class.simpleName}", th)
            ERROR
        }
    }
}

fun TestStage.asRetryable() = TestStage.RetryableTestStage(this)

fun TestStage.asErrorFree() = TestStage.ExceptionFreeTestStage(this)

class ChoosingUserAccountStage(private val userManagement: UserManagement) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(ChoosingUserAccountStage::class.java)
    }

    override suspend fun run(): TestContinuationType {
        val chosenUserId = userManagement.getRandomUserId(testCtx().serviceName)
        testCtx().userId = chosenUserId
        log.info("User for test is chosen $chosenUserId")
        return CONTINUE
    }

}

class OrderCreationStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderCreationStage::class.java)
    }

    override suspend fun run(): TestContinuationType {
        val order = serviceApi.createOrder(testCtx().userId!!)
        log.info("Order created: ${order.id}")
        testCtx().orderId = order.id
        return CONTINUE
    }
}

class OrderCollectingStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderCollectingStage::class.java)
    }

    override suspend fun run(): TestContinuationType {
        log.info("Adding items to order ${testCtx().orderId}")
        val itemIds = mutableSetOf<UUID>()
        repeat(Random.nextInt(50)) {
            val items = serviceApi.getItems()
            val itemToAdd = items.random()
            itemIds.add(itemToAdd.id)
            val amount = Random.nextInt(20)
            serviceApi.addItem(testCtx().orderId!!, itemToAdd.id, amount)
            val expectedItem = itemToAdd.copy(amount = amount)
            val resultAmount = serviceApi.getOrder(testCtx().orderId!!).itemsMap[expectedItem]
            if (resultAmount == null || resultAmount != amount) {
                log.error(
                    "Item was not added to the order ${testCtx().orderId}. " +
                            "Expected amount: $amount. Found: $resultAmount"
                )
                return FAIL
            }
        }
        val finalNumOfItems = serviceApi.getOrder(testCtx().orderId!!).itemsMap.size
        if (finalNumOfItems != itemIds.size) {
            log.error("Added number of items ($finalNumOfItems) doesn't match expected (${itemIds.size})")
        }
        log.info("Successfully added ${itemIds.size} items to order ${testCtx().orderId}")
        return CONTINUE
    }

}

class OrderAbandonedStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderAbandonedStage::class.java)
    }

    override suspend fun run(): TestContinuationType {
        val shouldBeAbandoned = Random.nextBoolean()
        if (shouldBeAbandoned) {
            val lastBucketTimestamp = serviceApi.getBucketAliveLogRecord(testCtx().orderId!!)
                .map { it.timestamp }
                .maxByOrNull { it } ?: 0
            delay(120_000) //todo shine2

            ConditionAwaiter.awaitAtMost(30, TimeUnit.SECONDS)
                .condition {
                    val bucketLogRecord = serviceApi.getBucketAliveLogRecord(testCtx().orderId!!)
                    bucketLogRecord.maxByOrNull { it.timestamp }?.timestamp ?: 0 > lastBucketTimestamp
                }
                .onFailure {
                    log.error("The order ${testCtx().orderId} was abandoned, but no records were found")
//                    return FAIL
                    throw IllegalArgumentException("Exception instead of silently fail")
                }.startWaiting()

            val recentLogRecord = serviceApi.getBucketAliveLogRecord(testCtx().orderId!!)
                .maxByOrNull { it.timestamp }

            if (recentLogRecord!!.userInteracted) {
                val order = serviceApi.getOrder(testCtx().orderId!!)
                if (order.status != OrderCollecting) {
                    log.error(
                        "User interacted with order ${testCtx().orderId}. " +
                                "Expected status - ${OrderCollecting::class.simpleName}, but was ${order.status}"
                    )
                    return FAIL
                }
            } else {
                ConditionAwaiter.awaitAtMost(15, TimeUnit.SECONDS)
                    .condition {
                        val order = serviceApi.getOrder(testCtx().orderId!!)
                        order.status == OrderDiscarded
                    }
                    .onFailure {
                        val order = serviceApi.getOrder(testCtx().orderId!!)
                        log.error(
                            "User didn't interact with order ${testCtx().orderId}" +
                                    "Expected status - ${OrderDiscarded::class.simpleName}, but was ${order.status}"
                        )
//                        return FAIL
                        throw IllegalArgumentException("Exception instead of silently fail")
                    }
            }

        }
        return CONTINUE
    }
}


class OrderFinalizingStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderFinalizingStage::class.java)
    }

    override suspend fun run(): TestContinuationType {
        log.info("Starting booking items stage for order ${testCtx().orderId}")
        val orderStateBeforeFinalizing = serviceApi.getOrder(testCtx().orderId!!)

        val bookingResult = serviceApi.finalizeOrder(testCtx().orderId!!)

        val orderStateAfterBooking = serviceApi.getOrder(testCtx().orderId!!)

        val bookingRecords = serviceApi.getBookingHistory(bookingResult.id)
        for (item in orderStateAfterBooking.itemsMap.keys) {
            if (bookingRecords.none { it.itemId == item.id }) {
                log.error("Cannot find booking log record: booking id = ${bookingResult.id}; itemId = ${item.id}; orderId = ${testCtx().orderId}")
                return FAIL
            }
        }

        when (orderStateAfterBooking.status) { //TODO Elina рассмотреть результат discard
            OrderStatus.OrderBooked -> {
                if (bookingResult.failedItems.isNotEmpty()) {
                    log.error("Order ${testCtx().orderId} is booked, but there are failed items")
                    return FAIL
                }

                for (item in orderStateAfterBooking.itemsMap.keys) {
                    val itemRecord = bookingRecords.firstOrNull { it.itemId == item.id }
                    if (itemRecord == null || itemRecord.status != BookingStatus.SUCCESS) {
                        log.error(
                            "Booking ${bookingResult.id} of order ${testCtx().orderId} is marked as successful, " +
                                    "but item ${item.id} is marked as ${itemRecord?.status}"
                        )
                        return FAIL
                    }
                }
                log.info("Successfully validated all items in BOOKED order ${testCtx().orderId}")
            }
            OrderCollecting -> {
                if (bookingResult.failedItems.isEmpty()) {
                    log.error("Booking of order ${testCtx().orderId} failed, but booking ${bookingResult.id} doesn't have failed items")
                    return FAIL
                }

                val failed = bookingRecords
                    .filter { it.status != BookingStatus.SUCCESS }
                    .map { it.itemId }
                    .toSet()

                if (failed != bookingResult.failedItems) {
                    log.error("List of failed items ${bookingResult.failedItems} doesn't match failed booking info of items $failed")
                    return FAIL
                }

                val failedList = orderStateAfterBooking.itemsMap.filter { it.key.id in failed }
                    .map { Triple(it.key.id, it.key.title, it.value) }

                log.info("Successfully validated all items in NOT BOOKED order ${testCtx().orderId}, failed items: $failedList")
                return STOP
            }
            else -> {
                log.error(
                    "Illegal transition for order ${orderStateAfterBooking.id} from ${orderStateBeforeFinalizing.status} " +
                            "to ${orderStateAfterBooking.status}"
                )
                return FAIL
            }
        }

        return CONTINUE
    }
}

class OrderPaymentStage(
    private val serviceApi: ServiceApi
) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderPaymentStage::class.java)
    }

    override suspend fun run(): TestContinuationType {
        val order = serviceApi.getOrder(testCtx().orderId!!)

        val paymentDetails = testCtx().paymentDetails
        paymentDetails.attempt++

        log.info("Payment started for order ${order.id}, attempt ${paymentDetails.attempt}")

        paymentDetails.startedAt = System.currentTimeMillis()

        val paidOrder = serviceApi.payOrder(testCtx().userId!!, testCtx().orderId!!)

        when (val status = paidOrder.paymentHistory.maxByOrNull { it.timestamp }!!.status) {
            SUCCESS -> {
                // todo elina check order is paid and user is charged

                ConditionAwaiter.awaitAtMost(5, TimeUnit.SECONDS)
                    .condition {
                        val financialRecords = serviceApi.getFinancialHistory(testCtx().userId!!, testCtx().orderId!!)
                        financialRecords.maxByOrNull { it.timestamp }?.type == FinancialOperationType.WITHDRAW
                    }
                    .onFailure {
                        log.error("Order ${order.id} is paid but there is not withdrawal operation found for user: ${testCtx().userId}")
//                        return FAIL
                        throw IllegalArgumentException("Exception instead of silently fail")
                    }.startWaiting()

                paymentDetails.finishedAt = System.currentTimeMillis()
                log.info("Payment succeeded for order ${order.id}, attempt ${paymentDetails.attempt}")
                return CONTINUE
            }
            FAILED -> { // todo sukhoa check order status hasn't changed and user ne charged
                if (paymentDetails.attempt < 5) {
                    log.info("Payment failed for order ${order.id}, go to retry. Attempt ${paymentDetails.attempt}")
                    return RETRY
                } else {
                    log.info("Payment failed for order ${order.id}, last attempt. Attempt ${paymentDetails.attempt}")
                    paymentDetails.failedAt = System.currentTimeMillis()
                    return FAIL
                }
            } // todo sukhoa not enough money
            else -> {
                log.error("Illegal transition for order ${order.id} from ${order.status} to $status")
                return FAIL
            }
        }
    }
}


data class TestParameters(
    val serviceName: String,
    val numberOfUsers: Int,
    val parallelProcessesNumber: Int
)

fun main() {
    val externalServiceMock = ExternalServiceSimulator(OrderStorage(), UserStorage(), ItemStorage())
    val userManagement = UserManagement(externalServiceMock, InternalAccountingService())

    val testApi = TestFlow(userManagement, externalServiceMock)

    runBlocking {
        val job = testApi.startTestingForService(TestParameters("test-service", 1, 1))

        delay(15_000)
        job.cancel()
        testApi.executor.shutdownNow()
    }
}