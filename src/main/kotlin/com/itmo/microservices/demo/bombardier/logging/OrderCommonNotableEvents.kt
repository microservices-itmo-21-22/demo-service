package com.itmo.microservices.demo.bombardier.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class OrderCommonNotableEvents(private val template: String) : NotableEvent {
    E_ILLEGAL_ORDER_TRANSITION("Illegal transition for order {} from {} to {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}