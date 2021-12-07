package com.itmo.microservices.demo.products.impl.repository

import com.itmo.microservices.demo.products.api.model.ProductModel
import com.itmo.microservices.demo.products.impl.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface ProductsRepository:JpaRepository<Product,String>{
    fun findAllByAmountGreaterThan(amount:Int):List<Product>
    fun findAllByAmountLessThan(amount:Int):List<Product>
    fun findById(id: UUID):Product?
}