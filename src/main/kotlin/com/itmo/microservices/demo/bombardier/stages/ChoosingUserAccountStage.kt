package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.flow.UserManagement

class ChoosingUserAccountStage(private val userManagement: UserManagement) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(ChoosingUserAccountStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
        val chosenUserId = userManagement.getRandomUserId(testCtx().serviceName)
        testCtx().userId = chosenUserId
        log.info("User for test is chosen $chosenUserId")
        return TestStage.TestContinuationType.CONTINUE
    }
}