package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.order.api.model.OrderModel
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import com.itmo.microservices.demo.order.impl.util.toModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*


@Service
class OrderServiceImpl(private val orderRepository: OrderRepository): OrderService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(OrderServiceImpl::class.java)
    }

   override fun createOrder(orderModel: OrderModel, author: UserDetails): OrderModel {
        var order = OrderEntity(
                date = orderModel.date
        )
        order = orderRepository.save(order)
        log.info("Order ${order.id} was created")
       return order.toModel()
    }

    override fun allOrders(): List<OrderModel> {
        return orderRepository.findAll().map { it.toModel() }
    }

    override fun getOrderById(orderId: UUID): OrderModel {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        return order.toModel()
    }

    override fun deleteOrderById(orderId: UUID) {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Busket $orderId not found")
        orderRepository.delete(order)
    }

}