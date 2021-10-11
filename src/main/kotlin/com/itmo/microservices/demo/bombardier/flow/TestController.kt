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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

class TestController(
    private val userManagement: UserManagement,
    private val serviceApi: ServiceApi
) {
    companion object {
        val log = LoggerFactory.getLogger(TestController::class.java)
    }

    val runningTests = ConcurrentHashMap<String, TestingFlow>()

    val executor: ExecutorService = Executors.newFixedThreadPool(4)

    private val coroutineScope = CoroutineScope(executor.asCoroutineDispatcher())

    private val testStages = listOf(
        ChoosingUserAccountStage(userManagement).asErrorFree(),
        OrderCreationStage(serviceApi).asErrorFree(),
        OrderCollectingStage(serviceApi).asErrorFree(),
//        OrderAbandonedStage(serviceApi).asErrorFree(),
        OrderFinalizingStage(serviceApi).asErrorFree(),
        OrderSettingDeliverySlotsStage(serviceApi).asErrorFree(),
        OrderChangeItemsAfterFinalizationStage(serviceApi),
        OrderPaymentStage(serviceApi).asRetryable().asErrorFree(),
//        OrderDeliveryStage(serviceApi).asErrorFree(),
    )

    fun startTestingForService(params: TestParameters) {
        if (runningTests.contains(params.serviceName)) {
            throw IllegalArgumentException("There is no such feature launch several flows for the service in parallel :(")
        }

        val testingFlowCoroutine = SupervisorJob()

        runBlocking {
            userManagement.createUsersPool(params.serviceName, params.numberOfUsers)
        }

        runningTests[params.serviceName] = TestingFlow(params, testingFlowCoroutine)

        repeat(params.parallelProcessesNumber) {
            log.info("Launch coroutine for ${params.serviceName}")
            launchNewTestFlow( params.serviceName)
        }
    }

    fun getTestingFlowForService(serviceName: String): TestingFlow {
        return runningTests[serviceName] ?: throw IllegalArgumentException("There is no running test for $serviceName")
    }

    class TestingFlow(
        val testParams: TestParameters,
        val testFlowCoroutine: CompletableJob,
        val testsPerformed: AtomicInteger = AtomicInteger(1)
    )

    private fun launchNewTestFlow(serviceName: String) {
        val testingFlow = runningTests[serviceName] ?: throw IllegalStateException("No running test found for :$serviceName")

        if (testingFlow.testParams.numberOfTests != null && testingFlow.testsPerformed.get() > testingFlow.testParams.numberOfTests) {
            log.info("Wrapping up test flow. Number of tests exceeded")
            return
        }
        val testNum = testingFlow.testsPerformed.getAndIncrement() // data race :(

        log.info("Starting $testNum test for service $serviceName, parent job is ${testingFlow.testFlowCoroutine}")

        coroutineScope.launch(testingFlow.testFlowCoroutine + TestContext(serviceName = serviceName)) {
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
    val parallelProcessesNumber: Int,
    val numberOfTests: Int? = null
)

fun main() {
    val externalServiceMock = ExternalServiceSimulator(OrderStorage(), UserStorage(), ItemStorage())
    val userManagement = UserManagement(externalServiceMock)

    val testApi = TestController(userManagement, externalServiceMock)

    runBlocking {
        testApi.startTestingForService(TestParameters("test-service", 1, 1, 5))
        delay(30_000)

        testApi.getTestingFlowForService("test-service").testFlowCoroutine.join()

        testApi.executor.shutdownNow()
    }
}