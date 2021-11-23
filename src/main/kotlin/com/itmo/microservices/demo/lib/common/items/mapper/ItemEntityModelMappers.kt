package com.itmo.microservices.demo.items.impl.util

import com.itmo.microservices.demo.items.api.model.CatalogItem
import com.itmo.microservices.demo.lib.common.items.entity.CatalogItemEntity

fun CatalogItem.toEntity(): CatalogItemEntity = CatalogItemEntity(
        title = this.title,
        description = this.description,
        price = this.price,
        amount = this.amount
)

fun CatalogItemEntity.toModel(): CatalogItem = CatalogItem(
        title = this.title,
        description = this.description,
        price = this.price,
        amount = this.amount
)
