package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.flow.ServiceApi

class OrderCreationStage(private val serviceApi: ServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderCreationStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
        val order = serviceApi.createOrder(testCtx().userId!!)
        log.info("Order created: ${order.id}")
        testCtx().orderId = order.id
        return TestStage.TestContinuationType.CONTINUE
    }
}