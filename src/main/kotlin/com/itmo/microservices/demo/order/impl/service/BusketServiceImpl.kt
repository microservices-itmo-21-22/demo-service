package com.itmo.microservices.demo.tasks.impl.service

import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.order.api.model.BusketDto
import com.itmo.microservices.demo.order.api.model.OrderProductDto
import com.itmo.microservices.demo.order.api.model.ProductType
import com.itmo.microservices.demo.order.api.service.BusketService
import com.itmo.microservices.demo.order.impl.entity.Busket
import com.itmo.microservices.demo.order.impl.entity.OrderItem
import com.itmo.microservices.demo.order.impl.repository.BusketRepository
import com.itmo.microservices.demo.order.impl.repository.ItemRepository
import com.itmo.microservices.demo.order.impl.util.toModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
@Suppress("UnstableApiUsage")
class BusketServiceImpl(private val productRepository: ItemRepository,
                        private val busketRepository: BusketRepository
) : BusketService {

    override fun allBuskets(): List<BusketDto> {
        return busketRepository.findAll().map { it.toModel() }
    }

    override fun createBusket(busket: BusketDto, author: UserDetails): BusketDto {
        val products = busket.products.mapNotNull { productRepository.findById(it).orElse(null) }.toMutableList()
        val busket = Busket(
            username = author.username,
            items = products
        )
        println(busket)
        busketRepository.save(busket)
        return busket.toModel()
    }

    override fun getBusketById(busketId: UUID): BusketDto {
        val busket = busketRepository.findByIdOrNull(busketId) ?: throw NotFoundException("Busket $busketId not found")
        return busket.toModel()
    }

    override fun deleteBusketById(busketId: UUID): BusketDto {
        val busket = busketRepository.findByIdOrNull(busketId) ?: throw NotFoundException("Busket $busketId not found")
        busketRepository.delete(busket)
        return busket.toModel()
    }

    override fun addProductToBusket(busketId: UUID, productId: UUID): BusketDto {
        val busket = busketRepository.findByIdOrNull(busketId) ?: throw NotFoundException("Busket $busketId not found")
        val product = productRepository.findByIdOrNull(productId) ?: throw NotFoundException("Product $productId not found")
        busket.items?.add(product)
        busketRepository.save(busket)
        return busket.toModel()
    }

    override fun deleteProductFromBusket(busketId: UUID, productId: UUID): BusketDto? {
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