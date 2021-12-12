package com.itmo.microservices.demo.payment.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class PaymentServiceNotableEvents(private val template: String) : NotableEvent {
    I_PAYMENT_CREATED("Payment submission created: {}"),
    I_FINANCIAL_LOGS_GIVEN("Financial logs sent for: {}"),
    I_MAKE_TRANSACTION_FAILURE("Couldn't make transaction : {}"),
    I_GET_TRANSACTION_FAILURE("Couldn't get transaction : {}")
    ;

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}