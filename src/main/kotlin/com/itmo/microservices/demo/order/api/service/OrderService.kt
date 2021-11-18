package com.itmo.microservices.demo.order.api.service

import com.itmo.microservices.demo.order.api.model.BookingDto
import com.itmo.microservices.demo.order.api.model.BusketDto
import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.payments.api.model.PaymentSubmissionDto
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface OrderService {
    fun allOrders(): List<OrderDto>
    fun getOrderById(orderId: UUID): OrderDto
    fun createOrder(userId: UserDetails): OrderDto
    fun addItemToOrder(orderId: UUID, itemId: UUID, amount: Int)
    fun registerOrder(orderId: UUID): BookingDto
    fun setDeliveryTime(orderId: UUID, slotinSec: Int): BookingDto
    fun deleteOrderById(orderId: UUID)
}