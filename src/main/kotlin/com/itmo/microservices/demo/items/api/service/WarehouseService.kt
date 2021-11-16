package com.itmo.microservices.demo.items.api.service

import com.itmo.microservices.demo.items.api.model.CatalogItem

interface WarehouseService {
    fun getCatalogItems(): List<CatalogItem>
}