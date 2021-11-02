package com.itmo.microservices.demo.products.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.products.api.messaging.ProductAddedEvent
import com.itmo.microservices.demo.products.api.messaging.ProductGotEvent
import com.itmo.microservices.demo.products.api.model.*
import com.itmo.microservices.demo.products.api.service.ProductsService
import com.itmo.microservices.demo.products.impl.entity.Product
import com.itmo.microservices.demo.products.impl.logging.ProductsServiceNotableEvents
import com.itmo.microservices.demo.products.impl.repository.ProductsRepository
import com.itmo.microservices.demo.products.impl.util.toModel
import org.springframework.stereotype.Service

@Suppress("UnstableApiUsage")
@Service
class DefaultProductsService(private val productsRepository: ProductsRepository,
                             private val eventBus: EventBus):ProductsService{

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun getAllProducts(): CatalogModel {
        eventBus.post(ProductGotEvent("all products got"))
        eventLogger.info(ProductsServiceNotableEvents.EVENT_PRODUCTS_GOT)
        return CatalogModel(products = productsRepository.findAll())
    }

    override fun addProduct(request: AddProductRequest) {
        val productEntity = productsRepository.save(request.toEntity())
        eventBus.post(ProductAddedEvent(productEntity.toModel()))
        eventLogger.info(ProductsServiceNotableEvents.EVENT_PRODUCT_ADDED)
    }


    fun AddProductRequest.toEntity(): Product =
        Product(
            name=this.name,
            description=this.description,
            country=this.country,
            price=this.price,
            sale=this.sale,
            type=this.type
        )

}