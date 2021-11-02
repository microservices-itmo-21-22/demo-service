package com.itmo.microservices.demo.products.impl.repository

import com.itmo.microservices.demo.products.api.model.ProductModel
import com.itmo.microservices.demo.products.impl.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ProductsRepository:JpaRepository<Product,String>