package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.bombardier.flow.ServiceApi
import com.itmo.microservices.demo.bombardier.logging.OrderCollectingNotableEvents.*
import org.springframework.stereotype.Component
import java.util.*
import kotlin.random.Random

@Component
class OrderCollectingStage(private val serviceApi: ServiceApi) : TestStage {
    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override suspend fun run(): TestStage.TestContinuationType {
        eventLogger.info(I_ADDING_ITEMS, testCtx().orderId)
        val itemIds = mutableSetOf<UUID>()
        repeat(Random.nextInt(50)) {
            val itemToAdd = serviceApi.getAvailableItems().random()
                .also { // todo should not to do on each addition but! we can randomise it
                    itemIds.add(it.id)
                }

            val amount = Random.nextInt(20)
            serviceApi.putItemToOrder(testCtx().orderId!!, itemToAdd.id, amount)

            val resultAmount = serviceApi.getOrder(testCtx().orderId!!).itemsMap
                .filter { it.key.id == itemToAdd.id }.values
                .firstOrNull()

            if (resultAmount == null || resultAmount != amount) {
                eventLogger.error(E_ADD_ITEMS_FAIL, testCtx().orderId, amount, resultAmount)
                return TestStage.TestContinuationType.FAIL
            }
        }

        val finalNumOfItems = serviceApi.getOrder(testCtx().orderId!!).itemsMap.size
        if (finalNumOfItems != itemIds.size) {
            eventLogger.error(E_ITEMS_MISMATCH, finalNumOfItems, itemIds.size)
            return TestStage.TestContinuationType.FAIL

        }
        eventLogger.info(I_ORDER_COLLECTING_SUCCESS, itemIds.size, testCtx().orderId)
        return TestStage.TestContinuationType.CONTINUE
    }

}