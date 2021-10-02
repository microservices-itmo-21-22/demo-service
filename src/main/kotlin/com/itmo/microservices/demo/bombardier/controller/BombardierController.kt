package com.itmo.microservices.demo.bombardier.controller

import com.itmo.microservices.demo.bombardier.external.ExternalServiceSimulator
import com.itmo.microservices.demo.bombardier.external.storage.ItemStorage
import com.itmo.microservices.demo.bombardier.external.storage.OrderStorage
import com.itmo.microservices.demo.bombardier.external.storage.UserStorage
import com.itmo.microservices.demo.bombardier.flow.TestController
import com.itmo.microservices.demo.bombardier.flow.TestParameters
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class BombardierController {
    val externalServiceMock = ExternalServiceSimulator(OrderStorage(), UserStorage(), ItemStorage())
    val userManagement = UserManagement(externalServiceMock)
    val testApi = TestController(userManagement, externalServiceMock)

    companion object {
        val logger = LoggerFactory.getLogger(BombardierController::class.java)
    }

    @GetMapping
    fun test() {
        runBlocking {
            testApi.startTestingForService(TestParameters("test-service", 1, 1, 5))

            testApi.getTestingFlowForService("test-service").testFlowCoroutine.complete()
            testApi.getTestingFlowForService("test-service").testFlowCoroutine.join()

            logger.info("Finished waiting for test job completion.")
//            testApi.executor.shutdownNow()
        }
    }
}