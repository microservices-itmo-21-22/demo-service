package com.itmo.microservices.demo.bombardier.controller

import com.itmo.microservices.demo.bombardier.dto.RunTestRequest
import com.itmo.microservices.demo.bombardier.external.ExternalServiceSimulator
import com.itmo.microservices.demo.bombardier.external.storage.ItemStorage
import com.itmo.microservices.demo.bombardier.external.storage.OrderStorage
import com.itmo.microservices.demo.bombardier.external.storage.UserStorage
import com.itmo.microservices.demo.bombardier.flow.TestController
import com.itmo.microservices.demo.bombardier.flow.TestParameters
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import com.itmo.microservices.demo.bombardier.dto.RunningTestsResponse
import com.itmo.microservices.demo.bombardier.dto.toExtended
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/test")
class BombardierController {
    private val testController = TestController()

    companion object {
        val logger = LoggerFactory.getLogger(BombardierController::class.java)
    }

    @GetMapping("running/{id}", produces = ["application/json"])
    @Operation(
        summary = "View info about running tests on service",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Test with name {id} was not found", responseCode = "404")
        ]
    )
    fun listRunningTestsPerService(@PathVariable id: String): RunningTestsResponse {
        val currentServiceTestFlow = testController.getTestingFlowForService(id)

        val testParamsExt = currentServiceTestFlow.testParams.toExtended(
            currentServiceTestFlow.testsStarted.get(),
            currentServiceTestFlow.testsFinished.get()
        )
        return RunningTestsResponse(listOf(testParamsExt))
    }

    @GetMapping("running/index", produces = ["application/json"])
    @Operation(
        summary = "View info about all services and their running tests",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
        ]
    )
    fun listAllRunningTests(): RunningTestsResponse {
        val currentTests = testController.runningTests
            .map { it.value.testParams.toExtended(
                it.value.testsStarted.get(),
                it.value.testsFinished.get())
            }
        return RunningTestsResponse(currentTests)
    }

    @PostMapping("/run")
    @Operation(
        summary = "Run Test with params",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(
                description = "There is no such feature launch several flows for the service in parallel",
                responseCode = "400",
            )
        ]
    )
    fun runTest(@RequestBody request: RunTestRequest) {
        testController.startTestingForService(
            TestParameters(
                request.serviceName,
                request.usersCount,
                request.parallelProcCount,
                request.testCount
            )
        )
        // testApi.getTestingFlowForService(request.serviceName).testFlowCoroutine.complete()
    }


    @PostMapping("/stop/{serviceName}")
    @Operation(
        summary = "Stop test by service name",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "There is no running test with current serviceName", responseCode = "400", content = [Content()])
        ]
    )
    fun stopTest(@PathVariable serviceName: String) {
        runBlocking {
            testController.stopTestByServiceName(serviceName)
        }
    }

    @PostMapping("/stopAll")
    @Operation(
        summary = "Stop all tests",
        responses = [
            ApiResponse(description = "OK", responseCode = "200")
        ]
    )
    fun stopAllTests() {
        runBlocking {
            testController.stopAllTests()
        }
    }
}