package com.itmo.microservices.demo.products.api.model

import java.util.*

data class AddProductRequest (
    val name: String,
    val description: String,
    val country: String,
    val price: Double,
    val sale: Double,
    val type: ProductType
)