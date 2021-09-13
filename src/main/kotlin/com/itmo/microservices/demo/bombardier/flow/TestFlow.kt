package com.itmo.microservices.demo.bombardier.flow

import com.itmo.microservices.demo.bombardier.external.ExternalServiceSimulator
import com.itmo.microservices.demo.bombardier.external.storage.ItemStorage
import com.itmo.microservices.demo.bombardier.external.storage.OrderStorage
import com.itmo.microservices.demo.bombardier.external.storage.UserStorage
import com.itmo.microservices.demo.bombardier.stages.*
import com.itmo.microservices.demo.bombardier.stages.TestStage.TestContinuationType.CONTINUE
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class TestFlow(
    private val userManagement: UserManagement,
    private val serviceApi: ServiceApi
) {
    companion object {
        val log = LoggerFactory.getLogger(TestFlow::class.java)
    }

    val executor: ExecutorService = Executors.newFixedThreadPool(4)

    private val coroutineScope = CoroutineScope(executor.asCoroutineDispatcher())

    private val testStages = listOf(
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

data class TestParameters(
    val serviceName: String,
    val numberOfUsers: Int,
    val parallelProcessesNumber: Int
)

fun main() {
    val externalServiceMock = ExternalServiceSimulator(OrderStorage(), UserStorage(), ItemStorage())
    val userManagement = UserManagement(externalServiceMock)

    val testApi = TestFlow(userManagement, externalServiceMock)

    runBlocking {
        val job = testApi.startTestingForService(TestParameters("test-service", 1, 1))

        delay(15_000)
        job.cancel()
        testApi.executor.shutdownNow()
    }
}