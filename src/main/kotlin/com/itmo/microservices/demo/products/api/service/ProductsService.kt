package com.itmo.microservices.demo.products.api.service

import com.itmo.microservices.demo.products.api.model.*
import com.itmo.microservices.demo.products.impl.entity.Product
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface ProductsService {
    fun getAllProducts(available: Boolean, userDetails: UserDetails?): List<Product>
    fun addProduct(request:ProductRequest): CatalogItemDto
    fun getProduct(id: UUID): Product
}