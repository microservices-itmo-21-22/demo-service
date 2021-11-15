package com.itmo.microservices.demo.common.metrics

import com.itmo.microservices.demo.bombardier.stages.TestStage
import io.micrometer.core.instrument.Counter
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

    fun testOkInc() {
        Counter.builder(testOkName).tags(*this.tags.toTypedArray())
            .register(Metrics.globalRegistry).increment()
    }

    fun testFailInc() {
        Metrics.counter(testFailName, *this.tags.toTypedArray()).increment()
    }

    suspend fun stageDurationRecord(stage: TestStage): TestStage.TestContinuationType {
        val startTime = System.currentTimeMillis()
        val res = stage.run()
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

    fun externalMethodDurationRecord(f: Callable<okhttp3.Response>): okhttp3.Response {
        val startTime = System.currentTimeMillis()
        val resp = f.call()
        val endTime = System.currentTimeMillis()

        Timer.builder("http_external_duration").publishPercentiles(0.95)
            .tags(*this.tags.toTypedArray(), "code", resp.code().toString())
            .register(Metrics.globalRegistry).record(endTime - startTime, TimeUnit.MILLISECONDS)

        return resp
    }

    private val testOkName = "test_counter_ok"
    private val testFailName = "test_counter_fail"
    private val stageDurationOkName = "stage_duration_ok"
    private val stageDurationFailName = "stage_duration_fail"

    val stageLabel = "stage"
    val serviceLabel = "service"
}