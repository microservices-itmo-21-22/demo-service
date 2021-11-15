package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.flow.TestCtxKey
import com.itmo.microservices.demo.bombardier.logging.UserNotableEvents
import kotlin.coroutines.coroutineContext

interface TestStage {
    suspend fun run(): TestContinuationType
    suspend fun testCtx() = coroutineContext[TestCtxKey]!!
    fun name(): String = this::class.simpleName!!

    class RetryableTestStage(private val wrapped: TestStage) : TestStage {
        override suspend fun run(): TestContinuationType {
            repeat(5) {
                when (val state = wrapped.run()) {
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
        override suspend fun run() = try {
            wrapped.run()
        } catch (failedException: TestStageFailedException) {
            TestContinuationType.FAIL
        } catch (th: Throwable) {
            var decoratedStage = wrapped
            while (decoratedStage is DecoratingStage) {
                decoratedStage = decoratedStage.wrapped
            }
            ChoosingUserAccountStage.eventLogger.error(UserNotableEvents.E_UNEXPECTED_EXCEPTION, wrapped::class.simpleName, th)
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