package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.delivery.api.model.BookingDto
import com.itmo.microservices.demo.delivery.impl.entity.BookingEntity
import com.itmo.microservices.demo.order.api.model.OrderModel
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultOrderService (
    val orderRepository: OrderRepository
        ) : OrderService {
    override fun createOrder(): OrderModel {
        TODO("Not yet implemented")
    }

    override fun getOrder(id: UUID): OrderModel {
        TODO("Not yet implemented")
    }

    override fun moveItemToCart(orderId: UUID, itemId: UUID, amount: Int) {
        TODO("Not yet implemented")
    }

    override fun finalizeOrder(id: UUID): BookingDto {
        TODO("Not yet implemented")
    }

    override fun setDeliverySlot(id: UUID, slotInSec: Int): BookingDto {
        TODO("Not yet implemented")
    }

    fun OrderEntity.toModel(): OrderModel {
        return OrderModel(
            this.id,
            this.timeCreated,
            this.status,
            this.itemsMap,
            this.deliveryDuration,
            this.paymentHistory
        )
    }

    fun BookingEntity.toModel(): BookingDto {
        return BookingDto(this.id, this.failedItems)
    }
}