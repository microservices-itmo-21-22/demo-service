package com.itmo.microservices.demo.products.impl.util

import com.itmo.microservices.demo.order.impl.entity.OrderItem
import com.itmo.microservices.demo.products.api.model.CatalogItemDto
import com.itmo.microservices.demo.products.api.model.ProductRequest
import com.itmo.microservices.demo.products.impl.entity.Product


fun Product.toModel(): CatalogItemDto = kotlin.runCatching {
    CatalogItemDto(
        id = this.id!!,
        name = this.title!!,
        description = this.description!!,
        price = this.price!!,
        amount = this.amount!!
    )
}.getOrElse { exception -> throw IllegalStateException("Some of product fields are null", exception) }

fun ProductRequest.toEntity(): OrderItem =
    OrderItem(
        this.title,
        this.description,
        this.price,
        this.amount
    )