package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.entities.Order
import com.itmo.microservices.demo.order.impl.repository.OrderItemRepository
import com.itmo.microservices.demo.order.impl.util.toModel
import com.itmo.microservices.demo.tasks.impl.repository.OrderRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultOrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository
    ) : OrderService {
    override fun getOrder(order_id: UUID): OrderDto {
        TODO("Not yet implemented")
    }

    override fun createOrder(user: UserDetails): OrderDto {
        //create base order
        val order = Order()
        //save base order, convert it to dto and return it
        return orderRepository.save(order).toModel(orderItemRepository)
    }
}