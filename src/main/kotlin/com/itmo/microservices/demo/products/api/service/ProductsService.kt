package com.itmo.microservices.demo.products.api.service

import com.itmo.microservices.demo.products.api.model.*
import com.itmo.microservices.demo.products.impl.entity.Product
import java.util.*

interface ProductsService {
    fun getAllProducts(available:Boolean):List<Product>
    fun addProduct(request:ProductRequest): CatalogItemDto
    fun getProduct(id: UUID): Product
    fun removeProduct(id: UUID, amountToRemove: Int): Boolean
}