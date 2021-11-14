package com.itmo.microservices.demo.bombardier.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class OrderCreationNotableEvents(private val template: String) : NotableEvent {
    I_ORDER_CREATED("Order created: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}