package com.itmo.microservices.demo.orders.impl.entity

import java.util.*

data class Order(
        val id : UUID,
        val status : Int,
        val basketId : UUID,
        val userId : UUID,
        val date : Date
) {
}