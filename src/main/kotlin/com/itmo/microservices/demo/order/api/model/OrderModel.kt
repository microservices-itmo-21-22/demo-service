package com.itmo.microservices.demo.order.api.model

import com.itmo.microservices.demo.order.impl.entity.Busket
import com.itmo.microservices.demo.users.impl.entity.AppUser
import java.util.*

data class OrderModel (
        val id: UUID?,
        val date: Date?,
//        val user: AppUser?,
        val busket: Busket?
)