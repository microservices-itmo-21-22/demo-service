package com.itmo.microservices.demo.orders.api.service

import com.itmo.microservices.demo.orders.api.model.OrderModel
import java.util.*

interface OrderService {
    fun getOrdersByUserId(userId : UUID) : List<OrderModel>
    fun getOrder(orderId : UUID) : OrderModel
}