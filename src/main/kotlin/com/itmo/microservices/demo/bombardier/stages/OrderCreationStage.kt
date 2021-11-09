package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.logging.OrderCreationNotableEvents.*
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi

class OrderCreationStage(private val externalServiceApi: ExternalServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderCreationStage::class.java)
        @InjectEventLogger
        private lateinit var eventLogger: EventLogger
    }

    override suspend fun run(): TestStage.TestContinuationType {
        val order = externalServiceApi.createOrder(testCtx().userId!!)
        log.info("Order created: ${order.id}")
        eventLogger.info(I_ORDER_CREATED, order.id)
        testCtx().orderId = order.id
        return TestStage.TestContinuationType.CONTINUE
    }
}