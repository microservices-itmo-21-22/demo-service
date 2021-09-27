package com.itmo.microservices.demo.bombardier.flow

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import kotlin.coroutines.coroutineContext

class CoroutineLoggingFactory {

    companion object {
        fun getLogger(clazz: Class<*>): CoroutineLogger {
            return CoroutineLogger(LoggerFactory.getLogger(clazz))
        }
    }

    class CoroutineLogger(val logger: Logger) {
        suspend fun info(message: String) {
            val testContext = coroutineContext[TestCtxKey]!!
            MDC.put("test_id", testContext.testId.toString())
            logger.info("[testId=${testContext.testId}] $message")
            MDC.clear()
        }

        suspend fun error(message: String, th: Throwable) {
            val testContext = coroutineContext[TestCtxKey]!!
            MDC.put("test_id", testContext.testId.toString())
            logger.error("[testId=${testContext.testId}] $message", th)
            MDC.clear()
        }

        suspend fun error(message: String) {
            val testContext = coroutineContext[TestCtxKey]!!
            MDC.put("test_id", testContext.testId.toString())
            logger.error("[testId=${testContext.testId}] $message")
            MDC.clear()
        }

    }
}