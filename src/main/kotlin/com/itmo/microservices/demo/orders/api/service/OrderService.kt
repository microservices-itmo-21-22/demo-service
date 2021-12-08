package com.itmo.microservices.demo.orders.api.service

import com.itmo.microservices.demo.orders.api.model.BookingDto
import com.itmo.microservices.demo.orders.api.model.OrderDto
import com.itmo.microservices.demo.orders.api.model.OrderModel
import com.itmo.microservices.demo.orders.api.model.PaymentModel
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface OrderService {
    fun createOrder() : OrderDto
    fun putItemToOrder(orderId : UUID, itemId : UUID, amount : Long) : ResponseEntity<Nothing>
    fun getOrder(orderId: UUID) :OrderDto
//    fun getOrdersByUsername(user : UserDetails) : List<OrderModel>
//    fun getOrder(orderId : UUID) : OrderModel
    fun book(orderId : UUID, user : UserDetails) : BookingDto
//    fun deleteOrder(orderId : UUID, user : UserDetails)
//    fun assignPayment(orderId : UUID, payment : PaymentModel)
}