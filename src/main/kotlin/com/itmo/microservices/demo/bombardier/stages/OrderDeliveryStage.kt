package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.flow.*
import com.itmo.microservices.demo.bombardier.utils.ConditionAwaiter
import kotlinx.coroutines.delay
import java.time.Duration
import java.util.concurrent.TimeUnit

class OrderDeliveryStage(
    private val serviceApi: ServiceApi
) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderDeliveryStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
        val orderBeforeDelivery = serviceApi.getOrder(testCtx().orderId!!)

        if (orderBeforeDelivery.status !is OrderStatus.OrderPayed) {
            log.error("Incorrect order ${orderBeforeDelivery.id} status before OrderDeliveryStage ${orderBeforeDelivery.status}")
            return TestStage.TestContinuationType.FAIL
        }

        serviceApi.simulateDelivery(testCtx().orderId!!)

        ConditionAwaiter.awaitAtMost(orderBeforeDelivery.deliveryDuration!!.toSeconds() + 3, TimeUnit.SECONDS)
            .condition {
                val updatedOrder = serviceApi.getOrder(testCtx().orderId!!)
                updatedOrder.status is OrderStatus.OrderDelivered ||
                        updatedOrder.status is OrderStatus.OrderRefund &&
                        serviceApi.userFinancialHistory(
                            testCtx().userId!!,
                            testCtx().orderId!!
                        ).last().type == FinancialOperationType.REFUND
            }
            .onFailure {
                log.error("Order status of order ${orderBeforeDelivery.id} not changed and no refund")
                throw TestStage.TestStageFailedException("Exception instead of silently fail")
            }
            .startWaiting()
        val orderAfterDelivery = serviceApi.getOrder(testCtx().orderId!!)
        when (orderAfterDelivery.status) {
            is OrderStatus.OrderDelivered -> {
                val deliveryLog = serviceApi.deliveryLog(testCtx().orderId!!)
                if (deliveryLog.outcome != DeliverySubmissionOutcome.SUCCESS) {
                    log.error("Delivery log for order ${orderAfterDelivery.id} is not DeliverySubmissionOutcome.SUCCESS")
                    return TestStage.TestContinuationType.FAIL
                }
                val expectedDeliveryTime = Duration.ofMillis(orderBeforeDelivery.paymentHistory.last().timestamp)
                    .plus(Duration.ofSeconds(orderBeforeDelivery.deliveryDuration.toSeconds()))
                if (orderAfterDelivery.status.deliveryFinishTime > expectedDeliveryTime.toMillis()) {
                    log.error("Delivery order ${orderAfterDelivery.id} was shipped at time = ${orderAfterDelivery.status.deliveryFinishTime} later than expected ${expectedDeliveryTime.toMillis()}")
                    return TestStage.TestContinuationType.FAIL
                }
                log.info("Order ${orderAfterDelivery.id} was successfully delivered")
            }
            is OrderStatus.OrderRefund -> {
                val userFinancialHistory = serviceApi.userFinancialHistory(testCtx().userId!!, testCtx().orderId!!)
                if (userFinancialHistory.filter { it.type == FinancialOperationType.WITHDRAW }.sumOf { it.amount } !=
                    userFinancialHistory.filter { it.type == FinancialOperationType.REFUND }.sumOf { it.amount }) {
                    log.error("Withdraw and refund amount are different for order ${orderAfterDelivery.id}, " +
                            "withdraw = ${
                                userFinancialHistory.filter { it.type == FinancialOperationType.WITHDRAW }
                                    .sumOf { it.amount }
                            }, " +
                            "refund = ${
                                userFinancialHistory.filter { it.type == FinancialOperationType.REFUND }
                                    .sumOf { it.amount }
                            }"
                    )
                }
                log.info("Refund for order ${orderAfterDelivery.id} is correct")
            }
            else -> {
                log.error(
                    "Illegal transition for order ${orderBeforeDelivery.id} from ${orderBeforeDelivery.status} " +
                            "to ${orderAfterDelivery.status}"
                )
                return TestStage.TestContinuationType.FAIL
            }
        }
        return TestStage.TestContinuationType.CONTINUE
    }
}