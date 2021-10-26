package com.itmo.microservices.demo.order.api.service

import com.itmo.microservices.demo.order.api.dto.OrderDTO
import com.itmo.microservices.demo.order.impl.entity.OrderEntity

interface OrderService {
    fun createOrder(order: OrderDTO)
    fun getOrders(): List<OrderEntity>
}