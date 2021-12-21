package com.itmo.microservices.demo.products.api.model

data class ProductRequest(
    val title: String,
    val description: String,
    val price: Int,
    val amount: Int
)