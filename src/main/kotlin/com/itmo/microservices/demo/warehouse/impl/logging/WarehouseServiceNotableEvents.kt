package com.itmo.microservices.demo.warehouse.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class WarehouseServiceNotableEvents(private val template: String): NotableEvent {
    I_ITEM_CREATED("Item created: {}"),
    I_ITEM_QUANTITY_UPDATED("Item updated: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}