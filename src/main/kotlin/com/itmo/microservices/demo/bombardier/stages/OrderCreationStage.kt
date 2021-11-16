package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.logging.OrderCreationNotableEvents.*
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import org.springframework.stereotype.Component

@Component
class OrderCreationStage : TestStage {
    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override suspend fun run(userManagement: UserManagement, externalServiceApi: ExternalServiceApi): TestStage.TestContinuationType {
        val order = externalServiceApi.createOrder(testCtx().userId!!)
        eventLogger.info(I_ORDER_CREATED, order.id)
        testCtx().orderId = order.id
        return TestStage.TestContinuationType.CONTINUE
    }
}