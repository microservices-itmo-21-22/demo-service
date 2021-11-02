package com.itmo.microservices.demo.products.impl.util

import com.itmo.microservices.demo.products.api.model.ProductModel
import com.itmo.microservices.demo.products.impl.entity.Product


fun Product.toModel():ProductModel = kotlin.runCatching {
    ProductModel(
        id =this.id!!,
        name=this.name!!,
    description = this.description!!,
    country = this.country!!,
    price = this.price!!,
    sale = this.sale!!,
    type = this.type!!)
}.getOrElse { exception -> throw IllegalStateException("Some of product fields are null", exception) }