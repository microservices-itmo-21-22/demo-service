package com.itmo.microservices.demo.delivery.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class DeliveryServiceNotableEvents(private val template: String) : NotableEvent {
    I_DELIVERY_CREATED("Delivery created: {}"),
    I_DELIVERY_DELETED("Delivery deleted: {}"),
    I_DELIVERY_DELIVERED("Delivery delivered: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}