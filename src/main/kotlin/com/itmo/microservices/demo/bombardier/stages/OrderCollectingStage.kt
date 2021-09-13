package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.flow.ServiceApi
import java.util.*
import kotlin.random.Random

class OrderCollectingStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderCollectingStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
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
                return TestStage.TestContinuationType.FAIL
            }
        }
        val finalNumOfItems = serviceApi.getOrder(testCtx().orderId!!).itemsMap.size
        if (finalNumOfItems != itemIds.size) {
            log.error("Added number of items ($finalNumOfItems) doesn't match expected (${itemIds.size})")
        }
        log.info("Successfully added ${itemIds.size} items to order ${testCtx().orderId}")
        return TestStage.TestContinuationType.CONTINUE
    }

}