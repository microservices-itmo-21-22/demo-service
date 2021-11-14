package com.itmo.microservices.demo.bombardier.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class OrderFinaizingNotableEvents(private val template: String) : NotableEvent {

    I_START_FINALIZING("Starting booking items stage for order {}"),
    E_BOOKING_LOG_RECORD_NOT_FOUND("Cannot find booking log record: booking id = {}; itemId = {}}; orderId = {}"),
    E_ORDER_HAS_FAIL_ITEMS("Order {} is booked, but there are failed items"),
    E_ITEMS_FAIL("Booking {} of order {} is marked as successful, but item {} is marked as {}"),
    I_SUCCESS_VALIDATE_BOOKED("Successfully validated all items in BOOKED order {}"),
    E_BOOKING_FAIL_BUT_ITEMS_SUCCESS("Booking of order {} failed, but booking {} doesn't have failed items"),
    E_LIST_FAILED_ITEMS_MISMATCH("List of failed items {} doesn't match failed booking info of items {}"),
    I_SUCCESS_VALIDATE_NOT_BOOKED("Successfully validated all items in NOT BOOKED order {}, failed items: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}