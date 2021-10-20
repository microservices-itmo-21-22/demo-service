package com.itmo.microservices.demo.stock.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class StockItemServiceNotableEvents(private val template: String) : NotableEvent {
    I_STOCK_ITEM_CREATED("Stock Item created: {}"),
    I_STOCK_ITEM_RESERVED("Stock Item reserved: {}"),
    I_STOCK_ITEM_DELETED("Stock Item deleted: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}