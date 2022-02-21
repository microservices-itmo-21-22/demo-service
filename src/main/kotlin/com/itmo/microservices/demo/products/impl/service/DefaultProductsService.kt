package com.itmo.microservices.demo.products.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.order.impl.entity.OrderItem
import com.itmo.microservices.demo.order.impl.repository.ItemRepository
import com.itmo.microservices.demo.products.api.messaging.ProductAddedEvent
import com.itmo.microservices.demo.products.api.messaging.ProductGotEvent
import com.itmo.microservices.demo.products.api.model.*
import com.itmo.microservices.demo.products.api.service.ProductsService
import com.itmo.microservices.demo.products.impl.entity.Product
import com.itmo.microservices.demo.products.impl.logging.ProductsServiceNotableEvents
import com.itmo.microservices.demo.products.impl.repository.ProductsRepository
import com.itmo.microservices.demo.products.impl.util.toModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Suppress("UnstableApiUsage")
@Service
class DefaultProductsService(private val productsRepository: ProductsRepository,
                             private val itemRepository: ItemRepository,
                             private val eventBus: EventBus):ProductsService{

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun getAllProducts(available: Boolean, userDetails: UserDetails?): List<Product> {
        if (userDetails == null) { throw AccessDeniedException("Access Denied") }

        eventBus.post(ProductGotEvent("All products got"))
        if(::eventLogger.isInitialized){
            eventLogger.info(ProductsServiceNotableEvents.EVENT_PRODUCTS_GOT)
        }
        return when(available) {
            true->productsRepository.findAllByAmountGreaterThan(0)
            false->productsRepository.findAllByAmountLessThan(1)
        }
    }

    override fun addProduct(productRequest: ProductRequest): CatalogItemDto {
        val productEntity = productsRepository.save(productRequest.toEntity())
        eventBus.post(ProductAddedEvent(productEntity.toModel()))
        if(::eventLogger.isInitialized){
            eventLogger.info(ProductsServiceNotableEvents.EVENT_PRODUCT_ADDED,productEntity.title)
        }
        val orderItem = productRequest.toOrderItem()
        itemRepository.save(orderItem)
        return productEntity.toModel()
    }

    override fun getProduct(id: UUID): Product =
        productsRepository.findByIdOrNull(id) ?: throw NotFoundException("Item $id not found")

    fun ProductRequest.toEntity():Product=
        Product(
            title= this.title,
            description = this.description,
            price = this.price,
            amount = this.amount
        )

    fun ProductRequest.toOrderItem() =
            OrderItem(
                    this.title,
                    this.description,
                    this.price,
                    this.amount
            )

}