package com.itmo.microservices.demo.order.api.service

import com.itmo.microservices.demo.delivery.api.model.BookingDto
import com.itmo.microservices.demo.order.api.model.OrderModel
import java.util.*

interface OrderService {
    fun createOrder(): OrderModel
    fun getOrder(id: UUID): OrderModel
    fun moveItemToCart(orderId: UUID, itemId: UUID, amount: Int)
    fun finalizeOrder(id: UUID): BookingDto
    fun setDeliverySlot(id: UUID, slotInSec: Int): BookingDto
}