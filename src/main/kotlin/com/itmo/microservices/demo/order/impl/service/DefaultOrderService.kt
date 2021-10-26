package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.notifications.impl.service.StubNotificationService
import com.itmo.microservices.demo.order.api.dto.OrderDTO
import com.itmo.microservices.demo.order.api.model.OrderModel
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.entity.Busket
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.order.impl.entity.OrderProduct
import com.itmo.microservices.demo.order.impl.repository.BusketRepository
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultOrderService(private val orderRepository: OrderRepository,
                          private val busketRepository: BusketRepository): OrderService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(StubNotificationService::class.java)
    }

    override fun createOrder(order: OrderDTO) {
        val busket = Busket(
                products = null,
                user = null,
                order = null
        )
        busketRepository.save(busket)
        val orderEntity = dtoToEntity(order)
        println(orderEntity.busket?.id)
        orderRepository.save(orderEntity)
        log.info("Order ${order.id} was created")
    }

    override fun getOrders(): List<OrderEntity> {
        return orderRepository.findAll()
    }

    private fun dtoToEntity(order: OrderDTO): OrderEntity = OrderEntity(
            date = order.date,
            busket = order.busketId?.let { busketRepository.findById(it) }?.orElse(null)
    )

    private fun modelToEntity(order: OrderModel): OrderEntity = OrderEntity(
            date = order.date,
            busket = order.busket
    )

}