package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.flow.Amount
import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.flow.OrderStatus
import com.itmo.microservices.demo.bombardier.flow.ServiceApi
import com.itmo.microservices.demo.bombardier.utils.ConditionAwaiter
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class OrderChangeItemsAfterFinalizationStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderChangeItemsAfterFinalizationStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
        val shouldRunStage = Random.nextBoolean()
        if (!shouldRunStage) {
            log.info("OrderChangeItemsAfterFinalizationStage will not be executed")
            return TestStage.TestContinuationType.CONTINUE
        }

        log.info("Starting change items after booked stage for order ${testCtx().orderId}")

        val itemToAdd = serviceApi.getAvailableItems().random()

        val amount: Amount = 13
        serviceApi.putItemToOrder(testCtx().orderId!!, itemToAdd.id, amount)

        ConditionAwaiter.awaitAtMost(3, TimeUnit.SECONDS)
            .condition {
                val theOrder = serviceApi.getOrder(testCtx().orderId!!)
                theOrder.itemsMap.any { it.key.id == itemToAdd.id && it.value == amount }
                        && theOrder.status == OrderStatus.OrderCollecting
            }
            .onFailure {
                log.error("Did not find item with correct number of items or order not in state collecting")
                throw TestStage.TestStageFailedException("Exception instead of silently fail")
            }.startWaiting()

        serviceApi.bookOrder(testCtx().orderId!!)

        ConditionAwaiter.awaitAtMost(3, TimeUnit.SECONDS)
            .condition {
                val theOrder = serviceApi.getOrder(testCtx().orderId!!)
                theOrder.status == OrderStatus.OrderBooked
            }
            .onFailure {
                log.error("Order is not booked")
                throw TestStage.TestStageFailedException("Exception instead of silently fail")
            }.startWaiting()

        serviceApi.deleteItemFromOrder(testCtx().orderId!!, itemToAdd.id, amount)

        ConditionAwaiter.awaitAtMost(3, TimeUnit.SECONDS)
            .condition {
                val theOrder = serviceApi.getOrder(testCtx().orderId!!)
                !theOrder.itemsMap.any { it.key.id == itemToAdd.id && it.value == amount }
                        && theOrder.status == OrderStatus.OrderCollecting
            }
            .onFailure {
                log.error("Deletion does not change order status or item was not deleted")
                throw TestStage.TestStageFailedException("Exception instead of silently fail")
            }.startWaiting()

        return TestStage.TestContinuationType.CONTINUE
    }
}