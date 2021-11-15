package com.itmo.microservices.demo.tasks.impl.service

import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.order.api.model.BusketModel
import com.itmo.microservices.demo.order.api.model.ProductType
import com.itmo.microservices.demo.order.api.service.BusketService
import com.itmo.microservices.demo.order.impl.entity.Busket
import com.itmo.microservices.demo.order.impl.entity.OrderItem
import com.itmo.microservices.demo.order.impl.repository.BusketRepository
import com.itmo.microservices.demo.order.impl.repository.OrderProductRepository
import com.itmo.microservices.demo.order.impl.util.toModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
@Suppress("UnstableApiUsage")
class BusketServiceImpl(private val productRepository: OrderProductRepository,
                        private val busketRepository: BusketRepository
) : BusketService {

    override fun allBuskets(): List<BusketModel> {
        productRepository.save(OrderItem(
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

    override fun createBusket(busket: BusketModel, author: UserDetails): BusketModel {
        val products = busket.products.mapNotNull { productRepository.findById(it).orElse(null) }.toMutableList()
        val busket = Busket(
            username = author.username,
            items = products
        )
        println(busket)
        busketRepository.save(busket)
        return busket.toModel()
    }

    override fun getBusketById(busketId: UUID): BusketModel {
        val busket = busketRepository.findByIdOrNull(busketId) ?: throw NotFoundException("Busket $busketId not found")
        return busket.toModel()
    }

    override fun deleteBusketById(busketId: UUID): BusketModel {
        val busket = busketRepository.findByIdOrNull(busketId) ?: throw NotFoundException("Busket $busketId not found")
        busketRepository.delete(busket)
        return busket.toModel()
    }

    override fun addProductToBusket(busketId: UUID, productId: UUID): BusketModel {
        val busket = busketRepository.findByIdOrNull(busketId) ?: throw NotFoundException("Busket $busketId not found")
        val product = productRepository.findByIdOrNull(productId) ?: throw NotFoundException("Product $productId not found")
        busket.items?.add(product)
        busketRepository.save(busket)
        return busket.toModel()
    }

    override fun deleteProductFromBusket(busketId: UUID, productId: UUID): BusketModel? {
        val busket = busketRepository.findByIdOrNull(busketId) ?: throw NotFoundException("Busket $busketId not found")
        productRepository.findByIdOrNull(productId) ?: throw NotFoundException("Product $productId not found")
        val id = busket.items?.indexOfFirst { it.id == productId } ?: -1
        if (id == -1) {
            return null
        }

        busket.items?.removeAt(id)
        busketRepository.save(busket)
        return busket.toModel()
    }
}