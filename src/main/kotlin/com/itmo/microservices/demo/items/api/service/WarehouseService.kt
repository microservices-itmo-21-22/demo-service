package com.itmo.microservices.demo.items.api.service

import com.itmo.microservices.demo.items.api.model.CatalogItem
import java.util.*

interface WarehouseService {
    fun getCatalogItems(available: Boolean): List<CatalogItem>
    fun getItem(itemId: UUID): CatalogItem?
    fun addItem(catalogItem: CatalogItem)
}