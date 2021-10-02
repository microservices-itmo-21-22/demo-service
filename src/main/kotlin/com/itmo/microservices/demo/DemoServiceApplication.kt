package com.itmo.microservices.demo

import com.itmo.microservices.demo.bombardier.external.ExternalServiceSimulator
import com.itmo.microservices.demo.bombardier.external.storage.ItemStorage
import com.itmo.microservices.demo.bombardier.external.storage.OrderStorage
import com.itmo.microservices.demo.bombardier.external.storage.UserStorage
import com.itmo.microservices.demo.bombardier.flow.TestController
import com.itmo.microservices.demo.bombardier.flow.TestParameters
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoServiceApplication

fun main(args: Array<String>) {
    runApplication<DemoServiceApplication>(*args)

    val externalServiceMock = ExternalServiceSimulator(OrderStorage(), UserStorage(), ItemStorage())
    val userManagement = UserManagement(externalServiceMock)

    val testApi = TestController(userManagement, externalServiceMock)

    runBlocking {
    }
}