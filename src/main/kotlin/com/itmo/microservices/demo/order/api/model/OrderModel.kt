package com.itmo.microservices.demo.order.api.model

import com.itmo.microservices.demo.payment.api.model.PaymentLogRecord
import java.util.*

data class OrderModel(
    val id: UUID?,
    val timeCreated: Long?,
    val status: OrderStatus?,
    var itemsMap: List<ItemMap>,
    var deliveryDuration: Int?,
    var paymentHistory: List<PaymentLogRecord>?
    )