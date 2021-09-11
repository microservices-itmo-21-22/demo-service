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
            ChoosingUserAccountStage(userManagement),
            OrderCreationStage(serviceApi),
            OrderCollectingStage(serviceApi), //бросание корзины, финализирование заказа (c возвратом всех зафейленных items). Если какой-то не получилось, то ничего не бронируем доставка
            OrderFinalizingStage(serviceApi),
            OrderPaymentStage(serviceApi).asRetryable()
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
    RETRY
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

}

fun TestStage.asRetryable() = TestStage.RetryableTestStage(this)

class ChoosingUserAccountStage(private val userManagement: UserManagement) : TestStage {
    companion object {
        val log = LoggerFactory.getLogger(ChoosingUserAccountStage::class.java)
    }

    override suspend fun run() = try {
        val chosenUserId = userManagement.getRandomUserId(testCtx().serviceName)
        testCtx().userId = chosenUserId
        log.info("User for test is chosen $chosenUserId")
        CONTINUE
    } catch (th: Throwable) {
        log.error("Unexpected in ${this::class.simpleName}", th)
        ERROR
    }

}

class OrderCreationStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = LoggerFactory.getLogger(OrderCreationStage::class.java)
    }

    override suspend fun run() = try {
        val order = serviceApi.createOrder(testCtx().userId!!)
        log.info("Order created: ${order.id}")
        testCtx().orderId = order.id
        CONTINUE
    } catch (th: Throwable) {
        log.error("Unexpected in ${this::class.simpleName}", th)
        ERROR
    }
}

class OrderCollectingStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = LoggerFactory.getLogger(OrderCollectingStage::class.java)
    }

    override suspend fun run() = try {
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
                log.error("Item was not added to the order ${testCtx().orderId}. " +
                        "Expected amount: $amount. Found: $resultAmount")
                FAIL
            }
        }
        val finalNumOfItems = serviceApi.getOrder(testCtx().orderId!!).itemsMap.size
        if (finalNumOfItems != itemIds.size) {
            log.error("Added number of items ($finalNumOfItems) doesn't match expected (${itemIds.size})")
        }
        log.info("Successfully added ${itemIds.size} items to order ${testCtx().orderId}")
        CONTINUE
    } catch (th: Throwable) {
        log.error("Unexpected in ${this::class.simpleName}", th)
        ERROR
    }

}

class OrderAbandonedStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = LoggerFactory.getLogger(OrderAbandonedStage::class.java)
    }

    override suspend fun run() = try {
        val shouldBeAbandoned = Random.nextBoolean()
        if (shouldBeAbandoned) {
            val lastBucketTimestamp = serviceApi.getBucketAliveLogRecord(testCtx().orderId!!)
                    .map { it.timestamp }
                    .maxByOrNull { it }?: 0
            delay(120_000) //todo shine2

            ConditionAwaiter.awaitAtMost(30, TimeUnit.SECONDS)
                    .condition {
                        val bucketLogRecord = serviceApi.getBucketAliveLogRecord(testCtx().orderId!!)
                        bucketLogRecord.maxByOrNull { it.timestamp }?.timestamp ?: 0 > lastBucketTimestamp
                    }
                    .onFailure {
                        log.error("The order ${testCtx().orderId} was abandoned, but no records were found")
                        FAIL
                    }

            val recentLogRecord = serviceApi.getBucketAliveLogRecord(testCtx().orderId!!)
                    .maxByOrNull { it.timestamp }

            if (recentLogRecord!!.userInteracted) {
                val order = serviceApi.getOrder(testCtx().orderId!!)
                if (order.status != OrderCollecting) {
                    log.error("User interacted with order ${testCtx().orderId}. " +
                            "Expected status - ${OrderCollecting::class.simpleName}, but was ${order.status}")
                    FAIL
                }
            } else {
                ConditionAwaiter.awaitAtMost(15, TimeUnit.SECONDS)
                        .condition {
                            val order = serviceApi.getOrder(testCtx().orderId!!)
                            order.status == OrderDiscarded
                        }
                        .onFailure {
                            val order = serviceApi.getOrder(testCtx().orderId!!)
                            log.error("User didn't interact with order ${testCtx().orderId}" +
                                    "Expected status - ${OrderDiscarded::class.simpleName}, but was ${order.status}")
                            FAIL
                        }
            }

        }
        CONTINUE
    } catch (th: Throwable) {
        OrderCollectingStage.log.error("Unexpected in ${this::class.simpleName}", th)
        ERROR
    }
}


class OrderFinalizingStage (private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = LoggerFactory.getLogger(OrderFinalizingStage::class.java)
    }

    override suspend fun run() = try {
        log.info("Staring booking items stage for order ${testCtx().orderId}")
        val originalOrder = serviceApi.getOrder(testCtx().orderId!!)

        val booking = serviceApi.finalizeOrder(testCtx().orderId!!)

        val finalOrder = serviceApi.getOrder(testCtx().orderId!!)

        for (item in finalOrder.itemsMap.keys) {
            val bookingRecord = item.bookingLogRecord.lastOrNull { it.bookingId == booking.id }
            if (bookingRecord == null) {
                log.error("Cannot find booking log record: booking id = ${booking.id}; " +
                        "itemId = ${item.id}; orderId = ${testCtx().orderId}")
                FAIL
            }
        }

        when(finalOrder.status) { //TODO Elina рассмотреть результат discard
            OrderStatus.OrderBooked -> {
                if (booking.failedItems.isNotEmpty()) {
                    log.error("Order ${testCtx().orderId} is booked, but there are failed items")
                    FAIL
                }

                for (item in finalOrder.itemsMap.keys) {
                    val bookingRecord = item.bookingLogRecord.last { it.bookingId == booking.id }
                    if (bookingRecord.status != BookingStatus.SUCCESS) {
                        log.error("Booking ${booking.id} of order ${testCtx().orderId} is marked as successful, " +
                                "but item ${item.id} is marked as ${bookingRecord.status}")
                        FAIL
                    }
                }
                log.info("Successfully validated all items in BOOKED order ${testCtx().orderId}")
            }
            OrderCollecting -> {
                if (booking.failedItems.isEmpty()) {
                    log.error("Booking of order ${testCtx().orderId} failed, but booking ${booking.id}" +
                            "doesn't have failed items")
                    FAIL
                }

                val failed = finalOrder.itemsMap.keys
                        .filter { item -> item.bookingLogRecord.last { it.bookingId == booking.id }.status != BookingStatus.SUCCESS }
                        .map { it.id }
                        .toSet()

                if(!failed.containsAll(booking.failedItems) ||  !booking.failedItems.containsAll(failed)) {
                    log.error("List of failed items doesn't match failed booking info of items ")
                    FAIL
                }

                log.info("Successfully validated all items in BOOKED order ${testCtx().orderId}")
            }
            else -> {
                log.error("Illegal transition for order ${finalOrder.id} from ${originalOrder.status} " +
                        "to ${finalOrder.status}")
                FAIL
            }
        }

        CONTINUE
    } catch (th: Throwable) {
        log.error("Unexpected in ${this::class.simpleName}", th)
        ERROR
    }

}

class OrderPaymentStage(
        private val serviceApi: ServiceApi
) : TestStage {
    companion object {
        val log = LoggerFactory.getLogger(OrderPaymentStage::class.java)
    }

    override suspend fun run() = try {
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
                            FAIL
                        }.startWaiting()

                paymentDetails.finishedAt = System.currentTimeMillis()
                log.info("Payment succeeded for order ${order.id}, attempt ${paymentDetails.attempt}")
                CONTINUE
            }
            FAILED -> { // todo sukhoa check order status hasn't changed and user ne charged
                if (paymentDetails.attempt < 5) {
                    log.info("Payment failed for order ${order.id}, go to retry. Attempt ${paymentDetails.attempt}")
                    RETRY
                } else {
                    log.info("Payment failed for order ${order.id}, last attempt. Attempt ${paymentDetails.attempt}")
                    paymentDetails.failedAt = System.currentTimeMillis()
                    FAIL
                }
            } // todo sukhoa not enough money
            else -> {
                log.error("Illegal transition for order ${order.id} from ${order.status} to $status")
                FAIL
            }
        }
    } catch (th: Throwable) {
        log.error("Unexpected in ${this::class.simpleName}", th)
        ERROR // todo sukhoa think of retry here
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

        delay(30_000)
        job.cancel()
        testApi.executor.shutdownNow()
    }
}