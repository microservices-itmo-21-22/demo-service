package com.itmo.microservices.demo.order.api.service

import com.itmo.microservices.demo.order.api.model.OrderDto
import java.util.*

interface OrderService {
    fun getOrder(order_id: UUID) : OrderDto
}