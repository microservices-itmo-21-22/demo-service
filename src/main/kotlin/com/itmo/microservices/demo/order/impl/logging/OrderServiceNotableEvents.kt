package com.itmo.microservices.demo.order.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class OrderServiceNotableEvents(private val template: String) : NotableEvent {
    I_ORDER_CREATED("Order created: {}"),
    I_ORDER_GOT("Order got: {}"),
    I_ITEM_ADDED_TO_ORDER("Item added to order: {}"),
    I_ORDER_REGISTERED("Order registered: {}"),
    I_ORDER_DATED("Order dated: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}