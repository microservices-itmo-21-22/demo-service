package com.itmo.microservices.demo.bombardier.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class OrderDeliveryNotableEvents(private val template: String) : NotableEvent {

    E_INCORRECT_ORDER_STATUS("Incorrect order {} status before OrderDeliveryStage {}"),
    E_NULL_DELIVERY_TIME("Incorrect order {}, deliveryDuration is null"),
    E_ORDER_STATUS_NOT_CHANGED_AND_NO_REFUND("Order status of order {} not changed and no refund"),
    E_DELIVERY_OUTCOME_FAIL("Delivery log for order {} is not DeliverySubmissionOutcome.SUCCESS"),
    E_DELIVERY_LATE("Delivery order {} was shipped at time = {} later than expected {}"),
    I_DELIVERY_SUCCESS("Order {} was successfully delivered"),
    E_WITHDRAW_AND_REFUND_DIFFERENT("Withdraw and refund amount are different for order {}, withdraw = {}, refund = {}"),
    I_REFUND_CORRECT("Refund for order {} is correct");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}