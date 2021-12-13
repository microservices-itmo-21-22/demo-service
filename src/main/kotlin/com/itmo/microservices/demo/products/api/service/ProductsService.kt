package com.itmo.microservices.demo.products.api.service

import com.itmo.microservices.demo.products.api.model.*
import com.itmo.microservices.demo.products.impl.entity.Product
import com.itmo.microservices.demo.users.api.model.RegistrationRequest

interface ProductsService {
    fun getAllProducts(available:Boolean):List<Product>
    fun getProductInfoById(id:String):ProductModel
    fun addProduct(request:AddProductrequest):ProductModel
}