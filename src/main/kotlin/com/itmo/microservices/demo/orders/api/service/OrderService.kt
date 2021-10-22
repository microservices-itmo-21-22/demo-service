package com.itmo.microservices.demo.orders.api.service

import com.itmo.microservices.demo.orders.api.model.OrderModel
import java.util.*

interface OrderService {
    fun getOrdersByUserId(userName : String) : List<OrderModel>
    fun getOrder(orderId : UUID) : OrderModel
    fun addOrder(order : OrderModel, userName : String)
    fun deleteOrder(orderId : UUID)
}