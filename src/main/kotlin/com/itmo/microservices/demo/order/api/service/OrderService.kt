package com.itmo.microservices.demo.order.api.service

import com.itmo.microservices.demo.order.api.model.Order

interface OrderService {
    fun createOrder(): Order
}