package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.delivery.api.model.BookingDto
import com.itmo.microservices.demo.delivery.impl.entity.BookingEntity
import com.itmo.microservices.demo.order.api.model.OrderModel
import com.itmo.microservices.demo.order.api.model.PaymentLogRecordDto
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.order.impl.entity.PaymentLogRecordEntity
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

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

    fun PaymentLogRecordEntity.toModel(): PaymentLogRecordDto {
        return PaymentLogRecordDto(
            this.timestamp,
            this.status,
            this.amount,
            this.transactionId
        )
    }

    fun OrderEntity.toModel(): OrderModel {
        return OrderModel(
            this.id,
            this.timeCreated,
            this.status,
            this.itemsMap.associate { it.id to it.amount },
            this.deliveryDuration,
            this.paymentHistory.stream()
                .map { item -> item.toModel() }
                .collect(Collectors.toList())
        )
    }

    fun BookingEntity.toModel(): BookingDto {
        return BookingDto(this.id, this.failedItems)
    }
}