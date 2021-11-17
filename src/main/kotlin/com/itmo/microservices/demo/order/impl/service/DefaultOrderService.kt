package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.items.api.service.WarehouseService
import com.itmo.microservices.demo.lib.common.order.dto.OrderDto
import com.itmo.microservices.demo.lib.common.order.dto.OrderItemDto
import com.itmo.microservices.demo.lib.common.order.dto.OrderStatusEnum
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.lib.common.order.entity.OrderEntity
import com.itmo.microservices.demo.lib.common.order.mapper.toEntity
import com.itmo.microservices.demo.lib.common.order.repository.OrderItemRepository
import com.itmo.microservices.demo.lib.common.order.mapper.toModel
import com.itmo.microservices.demo.lib.common.order.repository.OrderRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import org.webjars.NotFoundException

@Service
class DefaultOrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val itemService: WarehouseService
    ): OrderService {

    override fun getOrder(orderId: UUID): OrderDto {
        val optionalOrder = orderRepository.findById(orderId)
        if (!optionalOrder.isPresent) {
            throw NotFoundException("Order with Order ID $orderId not found")
        }
        return orderRepository.findById(orderId).get().toModel(orderItemRepository)
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
        order.status = OrderStatusEnum.SHIPPING
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
