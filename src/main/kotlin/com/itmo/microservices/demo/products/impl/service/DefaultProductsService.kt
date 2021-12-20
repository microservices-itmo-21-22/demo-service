package com.itmo.microservices.demo.products.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.products.api.messaging.ProductAddedEvent
import com.itmo.microservices.demo.products.api.messaging.ProductGotEvent
import com.itmo.microservices.demo.products.api.model.*
import com.itmo.microservices.demo.products.api.service.ProductsService
import com.itmo.microservices.demo.products.impl.entity.Product
import com.itmo.microservices.demo.products.impl.logging.ProductsServiceNotableEvents
import com.itmo.microservices.demo.products.impl.repository.ProductsRepository
import com.itmo.microservices.demo.products.impl.util.toModel
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct

@Suppress("UnstableApiUsage")
@Service
class DefaultProductsService(private val productsRepository: ProductsRepository,
                             private val eventBus: EventBus):ProductsService{

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun getAllProducts(available:Boolean): List<Product> {
        eventBus.post(ProductGotEvent("all products got"))
        if(::eventLogger.isInitialized){
            eventLogger.info(ProductsServiceNotableEvents.EVENT_PRODUCTS_GOT)
        }
        return when(available){
            true->productsRepository.findAllByAmountGreaterThan(0)
            false->productsRepository.findAllByAmountLessThan(1)
        }
    }

    override fun getProductInfoById(id:String):ProductModel=
        productsRepository.findById(UUID.fromString(id))?.toModel() ?:
        throw NotFoundException("Product with id $id not found")

    override fun addProduct(request:AddProductrequest): ProductModel {
    val productEntity = productsRepository.save(request.toEntity())
    eventBus.post(ProductAddedEvent(productEntity.toModel()))
        if(::eventLogger.isInitialized){
            eventLogger.info(ProductsServiceNotableEvents.EVENT_PRODUCT_ADDED,productEntity.title)
        }
        return productEntity.toModel()
    }


    @PostConstruct
    fun addItemsIntoDatabase(){
        for (i in 1..100000){
            productsRepository.save(Product("apple_${i}","A pear",100,1000000))
        }
    }




    fun AddProductrequest.toEntity():Product=
        Product(
            title= this.title,
            description = this.description,
            price = this.price,
            amount = this.amount
        )

}