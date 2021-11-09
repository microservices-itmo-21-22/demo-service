package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.items.impl.util.toEntity
import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.api.model.OrderItemDto
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

    override fun getOrder(orderId: UUID): OrderDto {
        return this.orderRepository.getById(orderId).toModel(orderItemRepository)
    }

    override fun createOrder(user: UserDetails): OrderDto {
        //create base order
        val orderEntity = OrderEntity()
        //save base order, convert it to dto and return it
        return orderRepository.save(orderEntity).toModel(orderItemRepository)
    }

    override fun submitOrder(user: UserDetails, orderId: UUID): OrderDto {
        val order = orderRepository.getById(orderId)
        // TODO add check delivery status from delivery service
        order.status = OrderStatus.SHIPPING;
        return orderRepository.save(order).toModel(orderItemRepository)
    }

    override fun addOrderItem(title: String, price: String, amount: Int, orderId: UUID) {
        val orderEntity = orderRepository.getById(orderId)

        val orderItemEntity = OrderItemDto(UUID.randomUUID(), title, price).toEntity(amount, orderEntity)
        orderItemRepository.save(orderItemEntity)
    }
}
