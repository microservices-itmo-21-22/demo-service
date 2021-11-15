package com.itmo.microservices.demo.order.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class OrderServiceNotableEvents(private val template: String) : NotableEvent {
    I_ORDER_CREATED("Order created: {}"),
    I_ORDER_GOT("Order got: {}"),
    I_ORDER_GOT_ALL("Order got all"),
    I_ORDER_DELETED("Task assigned: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}