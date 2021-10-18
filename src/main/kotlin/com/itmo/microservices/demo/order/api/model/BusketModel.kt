package com.itmo.microservices.demo.order.api.model

//import com.itmo.microservices.demo.order.impl.entity.Order
import com.itmo.microservices.demo.products.impl.entity.Product
import com.itmo.microservices.demo.users.impl.entity.AppUser
import java.util.*

data class BusketModel (
        val id: UUID?,
        val products: List<Product>?,
        val user: AppUser?
)