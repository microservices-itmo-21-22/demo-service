package com.itmo.microservices.demo.orders.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.sql.Time
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PaymentModel (
    val orderId : UUID? = null,
    val type : Int = 0,
    val amount : Int = 0,
    val time : Time? = null
)