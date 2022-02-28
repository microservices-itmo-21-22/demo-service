package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.flow.TestCtxKey
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import com.itmo.microservices.demo.bombardier.logging.UserNotableEvents
import com.itmo.microservices.demo.common.logging.EventLoggerWrapper
import com.itmo.microservices.demo.common.logging.testServiceFiledName
import net.logstash.logback.marker.Markers.append
import kotlin.coroutines.coroutineContext

interface TestStage {
    suspend fun run(userManagement: UserManagement, externalServiceApi: ExternalServiceApi): TestContinuationType
    suspend fun testCtx() = coroutineContext[TestCtxKey]!!
    fun name(): String = this::class.simpleName!!

    class RetryableTestStage(private val wrapped: TestStage) : TestStage {
        override suspend fun run(userManagement: UserManagement, externalServiceApi: ExternalServiceApi): TestContinuationType {
            repeat(5) {
                when (val state = wrapped.run(userManagement, externalServiceApi)) {
                    TestContinuationType.CONTINUE -> return state
                    TestContinuationType.FAIL -> return state
                    TestContinuationType.RETRY -> Unit
                }
            }
            return TestContinuationType.RETRY
        }

        override fun name(): String {
            return wrapped.name()
        }
    }

    private interface DecoratingStage {
        val wrapped: TestStage
    }

    class ExceptionFreeTestStage(override val wrapped: TestStage) : TestStage, DecoratingStage {
        override suspend fun run(userManagement: UserManagement, externalServiceApi: ExternalServiceApi) = try {
            wrapped.run(userManagement, externalServiceApi)
        } catch (failedException: TestStageFailedException) {
            TestContinuationType.FAIL
        } catch (th: Throwable) {
            var decoratedStage = wrapped
            while (decoratedStage is DecoratingStage) {
                decoratedStage = decoratedStage.wrapped
            }
            ChoosingUserAccountStage.eventLog.error(append(testServiceFiledName, testCtx().serviceName),UserNotableEvents.E_UNEXPECTED_EXCEPTION, wrapped::class.simpleName, th)
            TestContinuationType.ERROR
        }

        override fun name(): String {
            return wrapped.name()
        }
    }

    class TestStageFailedException(message: String) : IllegalStateException(message)

    enum class TestContinuationType {
        CONTINUE,
        FAIL,
        ERROR,
        RETRY,
        STOP;

        fun iSFailState(): Boolean {
            return this == FAIL || this == ERROR
        }
    }
}


fun TestStage.asRetryable() = TestStage.RetryableTestStage(this)

fun TestStage.asErrorFree() = TestStage.ExceptionFreeTestStage(this)