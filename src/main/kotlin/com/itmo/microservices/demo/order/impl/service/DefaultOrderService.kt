package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.items.api.service.ItemService
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
import org.webjars.NotFoundException

import java.util.*

@Service
class DefaultOrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val itemService: ItemService
    ): OrderService {

    override fun getOrder(order_id: UUID): OrderDto {
        val optionalOrder = orderRepository.findById(order_id)
        if (optionalOrder.isEmpty) {
            throw NotFoundException("Order with Order ID $order_id not found")
        }
        return orderRepository.findById(order_id).get().toModel(orderItemRepository)
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
        order.status = OrderStatus.SHIPPING
        return orderRepository.save(order).toModel(orderItemRepository)
    }

    override fun addItemToBasket(itemId: UUID, orderId: UUID, amount: Int) {
        val item = itemService.getItem(itemId)
            ?: throw NotFoundException("Item with item_id $itemId not found")

        val orderEntity = orderRepository.getById(orderId)
        val orderItemEntity = OrderItemDto(UUID.randomUUID(), item.title, item.price).toEntity(amount, orderEntity)
        orderItemRepository.save(orderItemEntity)
    }
}
