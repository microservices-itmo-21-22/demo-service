package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.flow.ServiceApi
import java.time.Duration
import kotlin.random.Random

class OrderSettingDeliverySlotsStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderSettingDeliverySlotsStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
        log.info("Choose delivery slot for order ${testCtx().orderId}")
        val availableSlots = serviceApi.getDeliverySlots(testCtx().orderId!!)

        var deliverySlot = Duration.ZERO
        repeat(Random.nextInt(10)) {
            deliverySlot = availableSlots.random()
            serviceApi.setDeliveryTime(testCtx().orderId!!, deliverySlot)

            val resultSlot = serviceApi.getOrder(testCtx().orderId!!).deliveryDuration
            if (resultSlot != deliverySlot) {
                log.error("Delivery slot was not chosen. Expected: $deliverySlot, Actual: $resultSlot")
                return TestStage.TestContinuationType.FAIL
            }
        }

        log.info("Successfully choose delivery slot: '${deliverySlot.seconds} sec' for order ${testCtx().orderId}")
        return TestStage.TestContinuationType.CONTINUE
    }
}