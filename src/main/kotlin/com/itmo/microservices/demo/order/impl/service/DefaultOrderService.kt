package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.entities.OrderEntity
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
    ): OrderService {

    override fun getOrder(order_id: UUID): OrderDto {
        return this.orderRepository.getById(order_id).toModel(orderItemRepository)
    }

    override fun createOrder(user: UserDetails): OrderDto {
        //create base order
        val orderEntity = OrderEntity()
        //save base order, convert it to dto and return it
        return orderRepository.save(orderEntity).toModel(orderItemRepository)
    }

    override fun submitOrder(user: UserDetails, order_id: UUID): OrderDto {
        val order = orderRepository.getById(order_id)
        // TODO add check delivery status from delivery service
        order.status = OrderStatus.SHIPPING;
        return orderRepository.save(order).toModel(orderItemRepository)
    }
}
