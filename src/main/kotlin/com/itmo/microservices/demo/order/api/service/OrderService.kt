package com.itmo.microservices.demo.order.api.service

import com.itmo.microservices.demo.order.api.model.OrderModel
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface OrderService {
    fun allOrders(): List<OrderModel>
    fun getOrderById(orderId: UUID): OrderModel
    fun deleteOrderById(orderId: UUID)
    fun createOrder(order: OrderModel, author: UserDetails)
}