package com.itmo.microservices.demo.order.api.controller

import com.itmo.microservices.demo.order.api.model.OrderModel
import com.itmo.microservices.demo.order.api.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/orders")
class OrderController(private var orderService: OrderService) {

    @GetMapping
    @Operation(
            summary = "Get all orders",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getOrders(): List<OrderModel> {
        return orderService.allOrders()
    }

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Получение заказа",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
                ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getOrderById(@PathVariable orderId: UUID) = orderService.getOrderById(orderId)

    @PostMapping
    @Operation(
            summary = "Создание нового заказа",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createOrder(@RequestBody order: OrderModel,
                    @Parameter(hidden = true) @AuthenticationPrincipal author: UserDetails) =
        orderService.createOrder(order, author)


    @DeleteMapping("/{orderId}")
    @Operation(
            summary = "Delete order by id",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
                ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteBusketById(@PathVariable orderId: UUID) =
            orderService.deleteOrderById(orderId)

}