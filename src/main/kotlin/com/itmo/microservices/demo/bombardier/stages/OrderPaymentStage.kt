package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.bombardier.external.FinancialOperationType
import com.itmo.microservices.demo.bombardier.external.OrderStatus
import com.itmo.microservices.demo.bombardier.external.PaymentStatus
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.flow.*
import com.itmo.microservices.demo.bombardier.logging.OrderCommonNotableEvents
import com.itmo.microservices.demo.bombardier.logging.OrderPaymentNotableEvents.*
import com.itmo.microservices.demo.bombardier.utils.ConditionAwaiter
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class OrderPaymentStage : TestStage {
    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override suspend fun run(userManagement: UserManagement, externalServiceApi: ExternalServiceApi): TestStage.TestContinuationType {
        val order = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!)

        val paymentDetails = testCtx().paymentDetails
        paymentDetails.attempt++

        eventLogger.info(I_PAYMENT_STARTED, order, paymentDetails.attempt)

        paymentDetails.startedAt = System.currentTimeMillis()

        val paymentSubmissionDto = externalServiceApi.payOrder(
            testCtx().userId!!,
            testCtx().orderId!!
        ) // todo sukhoa add payment details to test ctx

        ConditionAwaiter.awaitAtMost(6, TimeUnit.SECONDS)
            .condition {
                externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!).paymentHistory
                    .any { it.transactionId == paymentSubmissionDto.transactionId }
            }
            .onFailure {
                eventLogger.error(E_TIMEOUT_EXCEEDED, order.id)
                throw TestStage.TestStageFailedException("Exception instead of silently fail")
            }.startWaiting()

        val paymentLogRecord = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!).paymentHistory
            .find { it.transactionId == paymentSubmissionDto.transactionId }!!

        when (val status = paymentLogRecord.status) {
            PaymentStatus.SUCCESS -> {
                ConditionAwaiter.awaitAtMost(5, TimeUnit.SECONDS)
                    .condition {
                        externalServiceApi.getOrder(
                            testCtx().userId!!,
                            testCtx().orderId!!
                        ).status is OrderStatus.OrderPayed
                    }
                    .onFailure {
                        eventLogger.error(E_PAYMENT_STATUS_FAILED, order.id)
                        throw TestStage.TestStageFailedException("Exception instead of silently fail")
                    }.startWaiting()

                ConditionAwaiter.awaitAtMost(2, TimeUnit.SECONDS)
                    .condition {
                        val userChargedRecord =
                            externalServiceApi.userFinancialHistory(testCtx().userId!!, testCtx().orderId!!)
                                .find { it.paymentTransactionId == paymentSubmissionDto.transactionId }

                        userChargedRecord?.type == FinancialOperationType.WITHDRAW
                    }
                    .onFailure {
                        eventLogger.error(E_WITHDRAW_NOT_FOUND, order.id, testCtx().userId)
                        throw TestStage.TestStageFailedException("Exception instead of silently fail")
                    }.startWaiting()

                paymentDetails.finishedAt = System.currentTimeMillis()
                eventLogger.info(I_PAYMENT_SUCCESS, order.id, paymentDetails.attempt)

                return TestStage.TestContinuationType.CONTINUE
            }
            PaymentStatus.FAILED -> { // todo sukhoa check order status hasn't changed and user ne charged
                if (paymentDetails.attempt < 5) {
                    eventLogger.info(I_PAYMENT_RETRY, order.id, paymentDetails.attempt)
                    return TestStage.TestContinuationType.RETRY
                } else {
                    eventLogger.error(E_LAST_ATTEMPT_FAIL, order.id, paymentDetails.attempt)
                    paymentDetails.failedAt = System.currentTimeMillis()
                    return TestStage.TestContinuationType.FAIL
                }
            } // todo sukhoa not enough money
            else -> {
                eventLogger.error(
                    OrderCommonNotableEvents.E_ILLEGAL_ORDER_TRANSITION,
                    order.id, order.status, status
                )
                return TestStage.TestContinuationType.FAIL
            }
        }
    }
}