package com.itmo.microservices.demo.order.api.service

import com.itmo.microservices.demo.order.api.model.OrderDto
import org.hibernate.criterion.Order
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface OrderService {
    fun getOrder(order_id: UUID) : OrderDto
    fun createOrder(user: UserDetails) : OrderDto
    fun submitOrder(user: UserDetails, order_id: UUID): OrderDto
}
