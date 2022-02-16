package com.itmo.microservices.demo.items.impl.util

import com.itmo.microservices.demo.items.api.model.CatalogItemDto
import com.itmo.microservices.demo.lib.common.items.entity.CatalogItemEntity

fun CatalogItemDto.toEntity(): CatalogItemEntity = CatalogItemEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        price = this.price,
        amount = this.amount
)

fun CatalogItemEntity.toModel(): CatalogItemDto = CatalogItemDto(
        id = this.id,
        title = this.title,
        description = this.description,
        price = this.price,
        amount = this.amount
)
