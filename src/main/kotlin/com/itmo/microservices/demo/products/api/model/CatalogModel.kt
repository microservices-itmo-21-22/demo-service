package com.itmo.microservices.demo.products.api.model

import com.itmo.microservices.demo.products.impl.entity.Product
import javax.persistence.Id

data class CatalogModel (
        val products: List<Product>
        )