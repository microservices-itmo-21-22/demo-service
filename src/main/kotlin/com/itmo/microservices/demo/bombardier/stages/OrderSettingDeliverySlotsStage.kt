package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import java.time.Duration
import kotlin.random.Random

class OrderSettingDeliverySlotsStage(private val externalServiceApi: ExternalServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderSettingDeliverySlotsStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
        log.info("Choose delivery slot for order ${testCtx().orderId}")
        val availableSlots = externalServiceApi.getDeliverySlots(testCtx().userId!!, 10) // TODO: might be a better idea to provide different number here

        var deliverySlot = Duration.ZERO
        repeat(Random.nextInt(10)) {
            deliverySlot = availableSlots.random()
            externalServiceApi.setDeliveryTime(testCtx().userId!!, testCtx().orderId!!, deliverySlot)

            val resultSlot = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!).deliveryDuration
            if (resultSlot != deliverySlot) {
                log.error("Delivery slot was not chosen. Expected: $deliverySlot, Actual: $resultSlot")
                return TestStage.TestContinuationType.FAIL
            }
        }

        log.info("Successfully choose delivery slot: '${deliverySlot.seconds} sec' for order ${testCtx().orderId}")
        return TestStage.TestContinuationType.CONTINUE
    }
}