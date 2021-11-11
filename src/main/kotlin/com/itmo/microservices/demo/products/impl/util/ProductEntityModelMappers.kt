package com.itmo.microservices.demo.products.impl.util

import com.itmo.microservices.demo.products.api.model.ProductModel
import com.itmo.microservices.demo.products.impl.entity.Product


fun Product.toModel(): ProductModel = kotlin.runCatching {
    ProductModel(
        id = this.id!!,
        name = this.title!!,
        description = this.description!!,
        price = this.price!!,
        amount = this.amount!!
    )
}.getOrElse { exception -> throw IllegalStateException("Some of product fields are null", exception) }