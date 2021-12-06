package com.itmo.microservices.demo.orders.api.model

import com.itmo.microservices.demo.payment.api.model.PaymentLogRecordDto
import java.util.*

data class OrderModelDTO(

    val id : UUID? = null,
    val timeCreated : Long = 0,
    val status : OrderStatus,
    val itemsMap : Map<UUID, Int>,
    val deliveryDuration : Int?,
    val paymentHistory : List<PaymentLogRecordDto>
)