package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.bombardier.flow.ServiceApi
import com.itmo.microservices.demo.bombardier.logging.OrderCreationNotableEvents.*
import org.springframework.stereotype.Component

@Component
class OrderCreationStage(private val serviceApi: ServiceApi) : TestStage {
    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override suspend fun run(): TestStage.TestContinuationType {
        val order = serviceApi.createOrder(testCtx().userId!!)
        eventLogger.info(I_ORDER_CREATED, order.id)
        testCtx().orderId = order.id
        return TestStage.TestContinuationType.CONTINUE
    }
}