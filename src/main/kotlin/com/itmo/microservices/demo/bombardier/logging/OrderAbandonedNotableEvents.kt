package com.itmo.microservices.demo.bombardier.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class OrderAbandonedNotableEvents (private val template: String) : NotableEvent {
    E_ORDER_ABANDONED("The order {} was abandoned, but no records were found"),
    E_USER_INTERACT_ORDER("User interacted with order {}. " +
            "Expected status - {}, but was {}"),
    E_USER_DIDNT_INTERACT_ORDER("User didn't interacted with order {}. " +
            "Expected status - {}, but was {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}