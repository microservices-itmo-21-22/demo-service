package com.itmo.microservices.demo.items.api.service

import com.itmo.microservices.demo.items.api.model.CatalogItemDto
import java.util.*

interface WarehouseService {
    fun getCatalogItems(available: Boolean): List<CatalogItemDto>
    fun getItem(itemId: UUID): CatalogItemDto?
    // accepts an CatalogItemDto without an id and returns with
    fun addItem(catalogItemDto: CatalogItemDto) : CatalogItemDto
}