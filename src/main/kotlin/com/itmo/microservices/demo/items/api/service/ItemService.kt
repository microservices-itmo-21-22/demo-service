package com.itmo.microservices.demo.items.api.service

import com.itmo.microservices.demo.items.api.model.CatalogItem
import java.util.*

interface ItemService {
    fun getCatalogItems(): List<CatalogItem>
    fun addItemToBasket(itemId: UUID, orderId: UUID, amount: Int)
}