package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.flow.*
import com.itmo.microservices.demo.bombardier.utils.ConditionAwaiter
import java.util.concurrent.TimeUnit

class OrderPaymentStage(
    private val serviceApi: ServiceApi
) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderPaymentStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
        val order = serviceApi.getOrder(testCtx().orderId!!)

        val paymentDetails = testCtx().paymentDetails
        paymentDetails.attempt++

        log.info("Payment started for order ${order.id}, attempt ${paymentDetails.attempt}")

        paymentDetails.startedAt = System.currentTimeMillis()

        val paidOrder = serviceApi.payOrder(testCtx().userId!!, testCtx().orderId!!)

        when (val status = paidOrder.paymentHistory.maxByOrNull { it.timestamp }!!.status) {
            PaymentStatus.SUCCESS -> {
                // todo elina check order is paid and user is charged

                ConditionAwaiter.awaitAtMost(5, TimeUnit.SECONDS)
                    .condition {
                        val financialRecords = serviceApi.getFinancialHistory(testCtx().userId!!, testCtx().orderId!!)
                        financialRecords.maxByOrNull { it.timestamp }?.type == FinancialOperationType.WITHDRAW
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