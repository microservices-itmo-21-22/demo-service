package com.itmo.microservices.demo.bombardier.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class OrderChangeItemsAfterFinalizationNotableEvents(private val template: String) : NotableEvent {
    I_STATE_SKIPPED("OrderChangeItemsAfterFinalizationStage will not be executed for order {}"),
    I_START_CHANGING_ITEMS("Starting change items after booked stage for order {}"),
    E_ORDER_CHANGE_AFTER_FINALIZATION_FAILED("Did not find item {} with {} items in order {} or order not in state collecting");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}