package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.external.OrderStatus
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.logging.OrderChangeItemsAfterFinalizationNotableEvents.*
import com.itmo.microservices.demo.bombardier.utils.ConditionAwaiter
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class OrderChangeItemsAfterFinalizationStage(private val externalServiceApi: ExternalServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderChangeItemsAfterFinalizationStage::class.java)
        @InjectEventLogger
        private lateinit var eventLogger: EventLogger
    }

    override suspend fun run(): TestStage.TestContinuationType {
        val shouldRunStage = Random.nextBoolean()
        if (!shouldRunStage) {
            log.info("OrderChangeItemsAfterFinalizationStage will not be executed for order ${testCtx().orderId}")
            eventLogger.info(I_STATE_SKIPPED, testCtx().orderId)
            return TestStage.TestContinuationType.CONTINUE
        }

        log.info("Starting change items after booked stage for order ${testCtx().orderId}")
        eventLogger.info(I_START_CHANGING_ITEMS, testCtx().orderId)

        repeat(Random.nextInt(1, 10)) {
            val itemToAdd = externalServiceApi.getAvailableItems(testCtx().userId!!).random()

            val amount = Random.nextInt(1, 13)
            externalServiceApi.putItemToOrder(testCtx().userId!!, testCtx().orderId!!, itemToAdd.id, amount)

            ConditionAwaiter.awaitAtMost(3, TimeUnit.SECONDS)
                .condition {
                    val theOrder = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!)
                    theOrder.itemsMap.any { it.key.id == itemToAdd.id && it.value == amount }
                            && theOrder.status == OrderStatus.OrderCollecting
                }
                .onFailure {
                    log.error("Did not find item ${itemToAdd.id} with $amount items in order ${testCtx().orderId} or order not in state collecting")
                    eventLogger.error(E_ORDER_CHANGE_AFTER_FINALIZATION_FAILED, itemToAdd.id,amount, testCtx().orderId)
                    throw TestStage.TestStageFailedException("Exception instead of silently fail")
                }.startWaiting()
        }

        return TestStage.TestContinuationType.CONTINUE
    }
}