package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.flow.ServiceApi
import kotlin.random.Random

class OrderSettingDeliverySlotsStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderSettingDeliverySlotsStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
        log.info("Choose delivery slot for order")
        val availableSlots = serviceApi.getDeliverySlots(testCtx().orderId!!)

        var deliverySlot: Long = -1
        repeat(Random.nextInt(10)) {
            deliverySlot = availableSlots.random()
            serviceApi.setDeliveryTime(testCtx().orderId!!, deliverySlot)

            val resultSlot = serviceApi.getOrder(testCtx().orderId!!).deliveryDuration
            if (resultSlot != deliverySlot) {
                log.error("Delivery slot was not chosen. Expected: $deliverySlot, Actual: $resultSlot")
                return TestStage.TestContinuationType.FAIL
            }
        }

        log.info("Successfully choose delivery slot $deliverySlot for order ${testCtx().orderId}")
        return TestStage.TestContinuationType.CONTINUE
    }
}