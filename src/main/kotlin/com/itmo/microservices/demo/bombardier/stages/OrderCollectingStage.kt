package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import com.itmo.microservices.demo.bombardier.logging.OrderCollectingNotableEvents.*
import org.springframework.stereotype.Component
import java.util.*
import kotlin.random.Random

@Component
class OrderCollectingStage : TestStage {
    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override suspend fun run(userManagement: UserManagement, externalServiceApi: ExternalServiceApi): TestStage.TestContinuationType {
        eventLogger.info(I_ADDING_ITEMS, testCtx().orderId)

        val itemIds = mutableSetOf<UUID>()
        repeat(Random.nextInt(50)) {
            val itemToAdd = externalServiceApi.getAvailableItems(testCtx().userId!!).random()
                .also { // todo should not to do on each addition but! we can randomise it
                    itemIds.add(it.id)
                }

            val amount = Random.nextInt(20)
            externalServiceApi.putItemToOrder(testCtx().userId!!, testCtx().orderId!!, itemToAdd.id, amount)

            val resultAmount = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!).itemsMap
                .filter { it.key.id == itemToAdd.id }.values
                .firstOrNull()

            if (resultAmount == null || resultAmount != amount) {
                eventLogger.error(E_ADD_ITEMS_FAIL, testCtx().orderId, amount, resultAmount)
                return TestStage.TestContinuationType.FAIL
            }
        }

        val finalNumOfItems = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!).itemsMap.size
        if (finalNumOfItems != itemIds.size) {
            eventLogger.error(E_ITEMS_MISMATCH, finalNumOfItems, itemIds.size)
            return TestStage.TestContinuationType.FAIL

        }

        eventLogger.info(I_ORDER_COLLECTING_SUCCESS, itemIds.size, testCtx().orderId)
        return TestStage.TestContinuationType.CONTINUE
    }

}