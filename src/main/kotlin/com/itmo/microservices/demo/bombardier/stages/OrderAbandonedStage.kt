package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.utils.ConditionAwaiter
import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.external.OrderStatus
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class OrderAbandonedStage(private val externalServiceApi: ExternalServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderAbandonedStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
        val shouldBeAbandoned = Random.nextBoolean()
        if (shouldBeAbandoned) {
            val lastBucketTimestamp = externalServiceApi.abandonedCardHistory(testCtx().orderId!!)
                .map { it.timestamp }
                .maxByOrNull { it } ?: 0
            delay(120_000) //todo shine2

            ConditionAwaiter.awaitAtMost(30, TimeUnit.SECONDS)
                .condition {
                    val bucketLogRecord = externalServiceApi.abandonedCardHistory(testCtx().orderId!!)
                    bucketLogRecord.maxByOrNull { it.timestamp }?.timestamp ?: 0 > lastBucketTimestamp
                }
                .onFailure {
                    log.error("The order ${testCtx().orderId} was abandoned, but no records were found")
                    throw TestStage.TestStageFailedException("Exception instead of silently fail")
                }.startWaiting()

            val recentLogRecord = externalServiceApi.abandonedCardHistory(testCtx().orderId!!)
                .maxByOrNull { it.timestamp }

            if (recentLogRecord!!.userInteracted) {
                val order = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!)
                if (order.status != OrderStatus.OrderCollecting) {
                    log.error(
                        "User interacted with order ${testCtx().orderId}. " +
                                "Expected status - ${OrderStatus.OrderCollecting::class.simpleName}, but was ${order.status}"
                    )
                    return TestStage.TestContinuationType.FAIL
                }
            } else {
                ConditionAwaiter.awaitAtMost(15, TimeUnit.SECONDS)
                    .condition {
                        val order = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!)
                        order.status == OrderStatus.OrderDiscarded
                    }
                    .onFailure {
                        val order = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!)
                        log.error(
                            "User didn't interact with order ${testCtx().orderId}" +
                                    "Expected status - ${OrderStatus.OrderDiscarded::class.simpleName}, but was ${order.status}"
                        )
                        throw TestStage.TestStageFailedException("Exception instead of silently fail")
                    }
            }

        }
        return TestStage.TestContinuationType.CONTINUE
    }
}