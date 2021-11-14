package com.itmo.microservices.demo.bombardier.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class UserNotableEvents(private val template: String) : NotableEvent {

    I_USER_CHOSEN("User for test is chosen {}"),
    E_UNEXPECTED_EXCEPTION("Unexpected in {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}