package com.itmo.microservices.demo.common.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class CommonNotableEvents(private val template: String): NotableEvent {
    I_EVENT_BUS_SENT_MESSAGE_SUCCESS("Event bus sent message: {}"),
    I_LISTENER_RECEIVED_MESSAGE("Received message: {}"),
    W_WARN_EVENT("Warn situation. Description: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}
