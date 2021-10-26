package com.itmo.microservices.demo.items.impl.util

import com.itmo.microservices.demo.items.api.model.CatalogItem
import com.itmo.microservices.demo.items.impl.entity.CatalogItemEntity

fun CatalogItem.ToEntity(): CatalogItemEntity = CatalogItemEntity(
        title = this.title,
        description = this.description,
        price = this.price,
        amount = this.amount
)

fun CatalogItemEntity.ToModel(): CatalogItem = CatalogItem(
        title = this.title,
        description = this.description,
        price = this.price,
        amount = this.amount
)