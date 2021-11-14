package com.itmo.microservices.demo.bombardier.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class OrderCollectingNotableEvents(private val template: String) : NotableEvent {
    I_ADDING_ITEMS("Adding items to order {}"),
    E_ADD_ITEMS_FAIL("Item was not added to the order {}. Expected amount: {}. Found: {}"),
    E_ITEMS_MISMATCH("Added number of items {} doesn't match expected {}"),
    I_ORDER_COLLECTING_SUCCESS("Successfully added {} items to order {}");


    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}