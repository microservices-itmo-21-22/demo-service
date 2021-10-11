package com.itmo.microservices.demo.bombardier.dto

import com.itmo.microservices.demo.bombardier.flow.TestParameters

data class RunningTestsResponse(
    val runningTests: List<ExtendedTestsParameters>
)

data class ExtendedTestsParameters(
    val serviceName: String,
    val numberOfUsers: Int,
    val parallelProcessesNumber: Int,
    val numberOfTests: Int? = null,
    val testsStarted: Int,
    val testsFinished: Int
)

fun TestParameters.toExtended(testsStarted: Int, testsFinished: Int): ExtendedTestsParameters {
    return ExtendedTestsParameters(
        serviceName, numberOfUsers, parallelProcessesNumber, numberOfTests,
        testsStarted, testsFinished
    )
}