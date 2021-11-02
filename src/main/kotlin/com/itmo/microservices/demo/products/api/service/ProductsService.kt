package com.itmo.microservices.demo.products.api.service

import com.itmo.microservices.demo.products.api.model.*
import com.itmo.microservices.demo.users.api.model.RegistrationRequest

interface ProductsService {
    fun getAllProducts():CatalogModel
    fun addProduct(request: AddProductRequest)
}