package com.itmo.microservices.demo.test

import com.itmo.microservices.demo.test.PaymentStatus.FAILED
import com.itmo.microservices.demo.test.PaymentStatus.SUCCESS
import com.itmo.microservices.demo.test.TestContinuationType.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class TestService(
    private val userManagement: UserManagement,
    private val orderManagement: OrderManagement
) {
    companion object {
        val log = LoggerFactory.getLogger(TestService::class.java)
    }

    val executor = Executors.newFixedThreadPool(4)

    val coroutineScope = CoroutineScope(executor.asCoroutineDispatcher())

    val testStages = listOf(
        ChoosingUserAccountStage(userManagement),
        OrderCreationStage(orderManagement),
        OrderPaymentStage(orderManagement, userManagement).asRetryable()
    )

    fun startTestingForService(params: TestParameters) = coroutineScope.launch {
        userManagement.createUsersPool(params.serviceName, params.numberOfUsers)
        repeat(params.parallelProcessesNumber) {
            log.info("Launch coroutine for ${params.serviceName}")
            launchNewTestFlow(params.serviceName)
        }
    }

    private fun launchNewTestFlow(serviceName: String) {
        coroutineScope.launch(TestContext(serviceName)) {
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
    STOP,
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
                    STOP -> return state
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
        STOP
    }
}

class OrderCreationStage(private val orderManagement: OrderManagement) : TestStage {
    companion object {
        val log = LoggerFactory.getLogger(OrderCreationStage::class.java)
    }

    override suspend fun run() = try {
        val orderId = orderManagement.createOrder(testCtx().userId!!)
        log.info("Order created: $orderId")
        testCtx().orderId = orderId
        CONTINUE
    } catch (th: Throwable) {
        log.error("Unexpected in ${this::class.simpleName}", th)
        STOP
    }
}

class OrderPaymentStage(
    private val orderManagement: OrderManagement,
    private val userManagement: UserManagement
) : TestStage {
    companion object {
        val log = LoggerFactory.getLogger(OrderPaymentStage::class.java)
    }

    override suspend fun run() = try {
        val order = orderManagement.get(testCtx().orderId!!)

        val paymentDetails = testCtx().paymentDetails
        paymentDetails.attempt++

        log.info("Payment started for order ${order.id}, attempt ${paymentDetails.attempt}")

        paymentDetails.startedAt = System.currentTimeMillis()

        val paidOrder = orderManagement.payOrder(testCtx().userId!!, testCtx().orderId!!)

        when (val status = paidOrder.paymentHistory.maxByOrNull { it.timestamp }!!.status) {
            SUCCESS -> {
                // todo elina check order is paid and user is charged
                val financialRecords = userManagement.getFinancialRecords(testCtx().userId!!, testCtx().orderId!!)
                if (financialRecords.maxByOrNull { it.timestamp }!!.type != FinancialOperationType.WITHDRAW) {
                    log.error("Order ${order.id} is paid but there is not withdrawal operation found for user: ${testCtx().userId}")
                    STOP
                }

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
                    STOP
                }
            } // todo sukhoa not enough money
            else -> {
                log.error("Illegal transition for order ${order.id} from ${order.status} to $status")
                STOP
            }
        }
    } catch (th: Throwable) {
        log.error("Unexpected in ${this::class.simpleName}", th)
        STOP // todo sukhoa think of retry here
    }
}


data class TestParameters(
    val serviceName: String,
    val numberOfUsers: Int,
    val parallelProcessesNumber: Int
)


fun main() {
    val externalServiceMock = ExternalServiceSimulator(OrderStorage(), UserStorage())
    val userManagement = UserManagement(externalServiceMock, InternalAccountingService())
    val orderManagement = OrderManagement(externalServiceMock)

    val testApi = TestService(userManagement, orderManagement)

    runBlocking {
        testApi.startTestingForService(TestParameters("test-service", 1, 1))
        delay(5_000)
        testApi.executor.shutdownNow()
    }
}