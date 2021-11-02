package com.itmo.microservices.demo.items.api.service

import com.itmo.microservices.demo.items.api.model.CatalogItem

interface ItemService {
    fun getCatalogItems(): List<CatalogItem>
}