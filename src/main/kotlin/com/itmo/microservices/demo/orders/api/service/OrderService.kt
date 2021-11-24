package com.itmo.microservices.demo.orders.api.service

import com.itmo.microservices.demo.orders.api.model.OrderModel
import com.itmo.microservices.demo.orders.api.model.PaymentModel
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface OrderService {
    fun getOrdersByUsername(user : UserDetails) : List<OrderModel>
    fun getOrder(orderId : UUID) : OrderModel
    fun createOrderFromBusket(busketId : UUID, user : UserDetails) : OrderModel
    fun deleteOrder(orderId : UUID, user : UserDetails)
    fun assignPayment(orderId : UUID, payment : PaymentModel)
}