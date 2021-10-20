package com.itmo.microservices.demo.orders.api.controller

import com.itmo.microservices.demo.orders.api.model.OrderModel
import com.itmo.microservices.demo.orders.api.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/user")
class OrderController(private val orderService: OrderService) {

    @GetMapping("/{userId}/orders")
    @Operation(
            summary = "Get all orders",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getOrders(@PathVariable userId : UUID) : List<OrderModel> = orderService.getOrdersByUserId(userId)

    @GetMapping("/orders/{orderId}")
    @Operation(
            summary = "Get order by id",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getOrder(@PathVariable orderId : UUID) : OrderModel = orderService.getOrder(orderId)

}