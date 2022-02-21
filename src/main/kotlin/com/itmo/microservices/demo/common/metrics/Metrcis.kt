package com.itmo.microservices.demo.common.metrics

import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import com.itmo.microservices.demo.bombardier.stages.TestStage
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Timer
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit


class Metrics {
    private var tags = arrayListOf<String>()

    fun withTags(vararg tags: String): com.itmo.microservices.demo.common.metrics.Metrics {
        var m = com.itmo.microservices.demo.common.metrics.Metrics()
        m.tags = this.tags.clone() as ArrayList<String>
        m.tags.addAll(tags)
        return m
    }

    suspend fun stageDurationRecord(
        stage: TestStage,
        userManagement: UserManagement,
        externalServiceApi: ExternalServiceApi
    ): TestStage.TestContinuationType {
        val startTime = System.currentTimeMillis()
        val res = stage.run(userManagement, externalServiceApi)
        val endTime = System.currentTimeMillis()
        if (res.iSFailState()) {
            Timer.builder(stageDurationFailName).publishPercentiles(0.95).tags(*this.tags.toTypedArray())
                .register(Metrics.globalRegistry).record(endTime - startTime, TimeUnit.MILLISECONDS)
        } else {
            Timer.builder(stageDurationOkName).publishPercentiles(0.95).tags(*this.tags.toTypedArray())
                .register(Metrics.globalRegistry).record(endTime - startTime, TimeUnit.MILLISECONDS)
        }

        return res

    }

    fun testOkDurationRecord(timeMs: Long) {
        Timer.builder(testDurationOkName).publishPercentiles(0.95).tags(*this.tags.toTypedArray())
            .register(Metrics.globalRegistry).record(timeMs, TimeUnit.MILLISECONDS)
    }

    fun testFailDurationRecord(timeMs: Long) {
        Timer.builder(testDurationFailName).publishPercentiles(0.95).tags(*this.tags.toTypedArray())
            .register(Metrics.globalRegistry).record(timeMs, TimeUnit.MILLISECONDS)
    }


    fun externalMethodDurationRecord(timeMs: Long) {

        Timer.builder(externalCallDurationName).publishPercentiles(0.95)
            .tags(*this.tags.toTypedArray())
            .register(Metrics.globalRegistry).record(timeMs, TimeUnit.MILLISECONDS)
    }

    private val externalCallDurationName = "http_external_duration"
    private val stageDurationOkName = "stage_duration_ok"
    private val stageDurationFailName = "stage_duration_fail"
    private val testDurationOkName = "test_duration_ok"
    private val testDurationFailName = "test_duration_fail"


    val stageLabel = "stage"
    val serviceLabel = "service"
}