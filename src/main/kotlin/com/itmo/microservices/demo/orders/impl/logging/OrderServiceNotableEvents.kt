package com.itmo.microservices.demo.orders.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class OrderServiceNotableEvents(private val template: String) : NotableEvent {
    I_ORDER_CREATED("Order created: {}"),
    I_ORDER_DELETED("Order deleted: {}"),
    I_PAYMENT_ASSIGNED("Payment assigned: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}