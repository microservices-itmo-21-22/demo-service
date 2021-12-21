package com.itmo.microservices.demo.products.api.messaging

import com.itmo.microservices.demo.products.api.model.CatalogItemDto

data class ProductGotEvent(val description:String)

data class ProductAddedEvent(val product: CatalogItemDto)