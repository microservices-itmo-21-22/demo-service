package com.itmo.microservices.demo.orders.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrderModel (
    val id: UUID?,
    val basketId: UUID?,
    val date: Date?,
    val userId: UUID?,
    val status : Int
)