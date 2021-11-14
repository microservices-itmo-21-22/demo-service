package com.itmo.microservices.demo.bombardier.logging

import com.itmo.microservices.commonlib.logging.NotableEvent


enum class OrderPaymentNotableEvents(private val template: String) : NotableEvent {

    I_PAYMENT_STARTED("Payment started for order {}, attempt {}"),
    E_TIMEOUT_EXCEEDED("Payment is started for order: {} but hasn't finished withing 5 sec"),
    E_PAYMENT_STATUS_FAILED("There is payment record for order: {} for order status is different"),
    E_WITHDRAW_NOT_FOUND("Order {} is paid but there is not withdrawal operation found for user: {}"),
    I_PAYMENT_SUCCESS("Payment succeeded for order {}, attempt {}"),
    I_PAYMENT_RETRY("Payment failed for order {}, go to retry. Attempt {}"),
    E_LAST_ATTEMPT_FAIL("Payment failed for order {}, last attempt. Attempt {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}