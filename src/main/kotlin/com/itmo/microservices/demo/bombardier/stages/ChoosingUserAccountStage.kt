package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import com.itmo.microservices.demo.bombardier.logging.UserNotableEvents
import org.springframework.stereotype.Component

@Component
class ChoosingUserAccountStage : TestStage {
    companion object {
        @InjectEventLogger
        lateinit var eventLogger: EventLogger
    }

    override suspend fun run(userManagement: UserManagement, externalServiceApi: ExternalServiceApi): TestStage.TestContinuationType {
        val chosenUserId = userManagement.getRandomUserId(testCtx().serviceName)
        testCtx().userId = chosenUserId
        eventLogger.info(UserNotableEvents.I_USER_CHOSEN, chosenUserId)
        return TestStage.TestContinuationType.CONTINUE
    }
}