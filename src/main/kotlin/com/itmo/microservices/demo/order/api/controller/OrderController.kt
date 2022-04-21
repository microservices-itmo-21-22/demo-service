package com.itmo.microservices.demo.order.api.controller

import com.itmo.microservices.demo.order.api.model.Order
import com.itmo.microservices.demo.order.api.service.OrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    val service: OrderService
) {
    @PostMapping
    fun createOrder(): Order = service.createOrder()
}