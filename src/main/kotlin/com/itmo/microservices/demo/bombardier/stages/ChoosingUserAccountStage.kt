package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import com.itmo.microservices.demo.bombardier.logging.UserNotableEvents
import com.itmo.microservices.demo.common.logging.EventLoggerWrapper
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class ChoosingUserAccountStage : TestStage {
    companion object {
        @InjectEventLogger
        lateinit var eventLog: EventLogger
    }

    lateinit var eventLogger: EventLoggerWrapper

    override suspend fun run(userManagement: UserManagement, externalServiceApi: ExternalServiceApi): TestStage.TestContinuationType {
        eventLogger = EventLoggerWrapper(eventLog, testCtx().serviceName)

        val chosenUserId = userManagement.getRandomUserId(testCtx().serviceName)
        testCtx().userId = chosenUserId
        eventLogger.info(UserNotableEvents.I_USER_CHOSEN, chosenUserId)
        return TestStage.TestContinuationType.CONTINUE
    }
}