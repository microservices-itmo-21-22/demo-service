package com.itmo.microservices.demo.payments.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class PaymentServiceNotableEvents(private val template: String): NotableEvent {
    I_ORDER_PAID("Order paid: {}"),
    I_PAYMENT_GOT("Payment got: {}"),
    I_PAYMENTS_GOT("Payments got: {}"),
    I_EXTERNAL_SYSTEM_ERROR("External system returned system error: {}"),
    I_EXTERNAL_CLIENT_ERROR("External system rejected the request: {}"),
    I_EXTERNAL_SYSTEM_SUCCESS("External system returned success status: {}"),
    I_EXTERNAL_SYSTEM_FAILURE("External system returned failure status: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}