package com.itmo.microservices.demo.order.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.itmo.microservices.demo.order.impl.entity.OrderProduct
import com.itmo.microservices.demo.products.impl.entity.Product
import com.itmo.microservices.demo.users.impl.entity.AppUser
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BusketModel (
        val id: UUID?,
        val products: List<UUID>,
        val user: String?,
        val order: UUID?
)