package com.itmo.microservices.demo.test

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
        STOP
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
                    .onFailure { th ->
                        log.error("Order ${order.id} is paid but there is not withdrawal operation found for user: ${testCtx().userId}")
                        STOP
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

    val testApi = TestFlow(userManagement, externalServiceMock)

    runBlocking {
        val job = testApi.startTestingForService(TestParameters("test-service", 1, 1))

        delay(30_000)
        job.cancel()
//        testApi.executor.shutdownNow()
    }
}

class ConditionAwaiter(
    private val period: Long,
    private val unit: TimeUnit
) {
    companion object {
        fun awaitAtMost(period: Long, unit: TimeUnit) = ConditionAwaiter(period, unit)
    }

    private var condition: (suspend () -> Boolean)? = null

    private var successClosure: suspend () -> Unit = {}
    private var failureClosure: suspend (th: Throwable?) -> Unit = { th ->
        val message = "Condition is not fulfilled"
        if (th != null)
            throw IllegalArgumentException(message, th)
        else
            throw IllegalArgumentException(message)
    }

    fun condition(condition: suspend () -> Boolean): ConditionAwaiter {
        this.condition = condition
        return this
    }

    suspend fun startWaiting() {
        requireNotNull(condition) { "condition is null" }

        val waitUpTo = System.currentTimeMillis() + unit.toMillis(period)
        while (System.currentTimeMillis() <= waitUpTo) {
            try {
                if (condition!!()) {
                    successClosure.invoke()
                    return
                }
            } catch (th: Throwable) {
                failureClosure.invoke(th)
                return
            }
            delay(50)
        }
        failureClosure.invoke(null)
    }

    fun onSuccess(action: suspend () -> Unit): ConditionAwaiter {
        successClosure = action
        return this
    }

    fun onFailure(action: suspend (th: Throwable?) -> Unit): ConditionAwaiter {
        failureClosure = action
        return this
    }
}