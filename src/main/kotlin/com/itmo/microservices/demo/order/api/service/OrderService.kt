package com.itmo.microservices.demo.order.api.service

import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.api.model.OrderItemDto
import org.hibernate.criterion.Order
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface OrderService {
    fun getOrder(orderId: UUID) : OrderDto?
    fun createOrder(user: UserDetails) : OrderDto
    fun submitOrder(user: UserDetails, orderId: UUID): OrderDto

    fun addOrderItem(title: String, price: String, amount: Int, orderId: UUID)
}
