package com.itmo.microservices.demo.orders.impl.entity

import java.sql.Time
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Payment (
    @Id
    val orderId : UUID? = null,
    val type : Int = 0,
    val amount : Int = 0,
    val time : Time? = null
)