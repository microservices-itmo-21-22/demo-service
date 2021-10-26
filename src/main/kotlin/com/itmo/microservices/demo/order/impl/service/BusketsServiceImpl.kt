package com.itmo.microservices.demo.tasks.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.order.api.model.BusketModel
import com.itmo.microservices.demo.order.api.model.ProductType
import com.itmo.microservices.demo.order.api.service.BusketsService
import com.itmo.microservices.demo.order.impl.entity.Busket
import com.itmo.microservices.demo.order.impl.entity.OrderProduct
import com.itmo.microservices.demo.order.impl.repository.BusketRepository
import com.itmo.microservices.demo.order.impl.repository.OrderProductRepository
import com.itmo.microservices.demo.order.impl.util.toModel
import org.hibernate.criterion.Order
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
@Suppress("UnstableApiUsage")
class BusketsServiceImpl(private val productRepository: OrderProductRepository,
                         private val busketRepository: BusketRepository,
                         private val eventBus: EventBus
) : BusketsService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun allBuskets(): List<BusketModel> {
        productRepository.save(OrderProduct(
            name = "Галоши",
            description = "Крутые галоши",
            country = "Русские",
            price = 300.0,
            sale = null,
            type = ProductType.CLOTHES
        ))
        val products = productRepository.findAll()
        println(products)
        return busketRepository.findAll().map { it.toModel() }
    }

    override fun createBusket(busket: BusketModel, author: UserDetails) {
        val products = busket.products.mapNotNull { productRepository.findById(it).orElse(null) }.toMutableList()
        val busket = Busket(
            username = author.username,
            products = products
        )
        println(busket)
        busketRepository.save(busket)
    }

    override fun getBusketById(busketId: UUID): BusketModel {
        val busket = busketRepository.findByIdOrNull(busketId) ?: throw NotFoundException("Busket $busketId not found")
        return busket.toModel()
    }

    override fun deleteBusketById(busketId: UUID) {
        val busket = busketRepository.findByIdOrNull(busketId) ?: throw NotFoundException("Busket $busketId not found")
        busketRepository.delete(busket)
    }

    override fun addProductToBusket(busketId: UUID, productId: UUID) {
        val busket = busketRepository.findByIdOrNull(busketId) ?: throw NotFoundException("Busket $busketId not found")
        val product = productRepository.findByIdOrNull(productId) ?: throw NotFoundException("Product $productId not found")
        busket.products?.add(product)
        busketRepository.save(busket)
    }

    override fun deleteProductFromBusket(busketId: UUID, productId: UUID) {
        val busket = busketRepository.findByIdOrNull(busketId) ?: throw NotFoundException("Busket $busketId not found")
        val product = productRepository.findByIdOrNull(productId) ?: throw NotFoundException("Product $productId not found")
        busket.products?.remove(product)
        busketRepository.save(busket)
    }
}