package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.bombardier.flow.ServiceApi
import com.itmo.microservices.demo.bombardier.logging.OrderSettingsDeliveryNotableEvents.*
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.random.Random

@Component
class OrderSettingDeliverySlotsStage(private val serviceApi: ServiceApi) : TestStage {
    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override suspend fun run(): TestStage.TestContinuationType {
        eventLogger.info(I_CHOOSE_SLOT, testCtx().orderId)
        val availableSlots = serviceApi.getDeliverySlots(testCtx().orderId!!)

        var deliverySlot = Duration.ZERO
        repeat(Random.nextInt(10)) {
            deliverySlot = availableSlots.random()
            serviceApi.setDeliveryTime(testCtx().orderId!!, deliverySlot)

            val resultSlot = serviceApi.getOrder(testCtx().orderId!!).deliveryDuration
            if (resultSlot != deliverySlot) {
                eventLogger.error(E_CHOOSE_SLOT_FAIL, deliverySlot, resultSlot)
                return TestStage.TestContinuationType.FAIL
            }
        }

        eventLogger.info(I_CHOOSE_SLOT_SUCCESS, deliverySlot.seconds, testCtx().orderId)
        return TestStage.TestContinuationType.CONTINUE
    }
}