package com.itmo.microservices.demo.payments.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class PaymentServiceNotableEvents(private val template: String): NotableEvent {
    I_ORDER_PAID("Order paid: {}"),
    I_PAYMENT_GOT("Payment got: {}"),
    I_PAYMENTS_GOT("Payments got: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}