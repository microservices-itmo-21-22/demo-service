package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.external.FinancialOperationType
import com.itmo.microservices.demo.bombardier.external.OrderStatus
import com.itmo.microservices.demo.bombardier.external.PaymentStatus
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.flow.*
import com.itmo.microservices.demo.bombardier.utils.ConditionAwaiter
import java.util.concurrent.TimeUnit

class OrderPaymentStage(
    private val externalServiceApi: ExternalServiceApi
) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderPaymentStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
        val order = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!)

        val paymentDetails = testCtx().paymentDetails
        paymentDetails.attempt++

        log.info("Payment started for order ${order.id}, attempt ${paymentDetails.attempt}")

        paymentDetails.startedAt = System.currentTimeMillis()

        val paymentSubmissionDto = externalServiceApi.payOrder(testCtx().userId!!, testCtx().orderId!!) // todo sukhoa add payment details to test ctx

        ConditionAwaiter.awaitAtMost(6, TimeUnit.SECONDS)
            .condition {
                externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!).paymentHistory
                    .any {it.transactionId == paymentSubmissionDto.transactionId}
            }
            .onFailure {
                log.error("Payment is started for order: ${order.id} but hasn't finished withing 5 sec")
                throw TestStage.TestStageFailedException("Exception instead of silently fail")
            }.startWaiting()

        val paymentLogRecord = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!).paymentHistory
            .find { it.transactionId == paymentSubmissionDto.transactionId }!!

        when (val status = paymentLogRecord.status) {
            PaymentStatus.SUCCESS -> {
                ConditionAwaiter.awaitAtMost(5, TimeUnit.SECONDS)
                    .condition {
                        externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!).status is OrderStatus.OrderPayed
                    }
                    .onFailure {
                        log.error("There is payment record for order: ${order.id} for order status is different")
                        throw TestStage.TestStageFailedException("Exception instead of silently fail")
                    }.startWaiting()

                ConditionAwaiter.awaitAtMost(2, TimeUnit.SECONDS)
                    .condition {
                        val userChargedRecord = externalServiceApi.userFinancialHistory(testCtx().userId!!, testCtx().orderId!!)
                            .find { it.paymentTransactionId == paymentSubmissionDto.transactionId }

                        userChargedRecord?.type == FinancialOperationType.WITHDRAW
                    }
                    .onFailure {
                        log.error("Order ${order.id} is paid but there is not withdrawal operation found for user: ${testCtx().userId}")
                        throw TestStage.TestStageFailedException("Exception instead of silently fail")
                    }.startWaiting()

                paymentDetails.finishedAt = System.currentTimeMillis()
                log.info("Payment succeeded for order ${order.id}, attempt ${paymentDetails.attempt}")
                return TestStage.TestContinuationType.CONTINUE
            }
            PaymentStatus.FAILED -> { // todo sukhoa check order status hasn't changed and user ne charged
                if (paymentDetails.attempt < 5) {
                    log.info("Payment failed for order ${order.id}, go to retry. Attempt ${paymentDetails.attempt}")
                    return TestStage.TestContinuationType.RETRY
                } else {
                    log.info("Payment failed for order ${order.id}, last attempt. Attempt ${paymentDetails.attempt}")
                    paymentDetails.failedAt = System.currentTimeMillis()
                    return TestStage.TestContinuationType.FAIL
                }
            } // todo sukhoa not enough money
            else -> {
                log.error("Illegal transition for order ${order.id} from ${order.status} to $status")
                return TestStage.TestContinuationType.FAIL
            }
        }
    }
}