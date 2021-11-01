package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.order.api.dto.Booking
import com.itmo.microservices.demo.order.api.dto.Order
import java.util.*

interface IOrderService {
    fun createOrder(order: Order?): Order?
    fun getOrderById(orderId: UUID?): Order?
    fun updateOrder(orderId: UUID?, itemId: UUID?, amount: Int)
    fun book(orderId: UUID?): Booking?
    fun selectDeliveryTime(orderId: UUID?, seconds: Int)
}