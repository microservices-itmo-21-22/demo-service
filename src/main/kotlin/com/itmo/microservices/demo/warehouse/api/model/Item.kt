package com.itmo.microservices.demo.warehouse.api.model

import java.util.*

data class Item(
    val id: UUID?,
    val title: String?,
    val description: String?,
    val price: Int? = 100,
    val amount: Int?
)
