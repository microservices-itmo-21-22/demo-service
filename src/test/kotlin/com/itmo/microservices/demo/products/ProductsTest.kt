package com.itmo.microservices.demo.products

import com.google.common.eventbus.EventBus
import com.itmo.microservices.demo.products.impl.entity.Product
import com.itmo.microservices.demo.products.impl.repository.ProductsRepository
import com.itmo.microservices.demo.products.impl.service.DefaultProductsService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*


class ProductsTest {
    private val productsRepository = Mockito.mock(ProductsRepository::class.java)
    private val eventBus = Mockito.mock(EventBus::class.java)
    private val productsId = UUID.randomUUID()

    private fun productsMock(): Product {
        return Product("car","a car",1000000,10).also { it.id = productsId }
    }


//    @Test
//    fun getProductCatalog() {
//        val productsService = DefaultProductsService(productsRepository, eventBus)
//        Mockito.`when`(productsRepository.findAllByAmountGreaterThan(0)).thenReturn(mutableListOf(productsMock()))
//        val actual = productsService.getAllProducts(true).map{it.toString()}
//        val expected = listOf(productsMock().toString())
//        Assertions.assertEquals(actual, expected)
//    }
}