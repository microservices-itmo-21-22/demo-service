package com.itmo.microservices.demo.orders.api.controller

import com.itmo.microservices.demo.orders.api.model.OrderModel
import com.itmo.microservices.demo.orders.api.model.PaymentModel
import com.itmo.microservices.demo.orders.api.service.OrderService
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
@RequestMapping("/user")
class OrderController(private val orderService: OrderService) {

    @GetMapping("/{userName}/orders")
    @Operation(
            summary = "Get all orders",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getOrders(@PathVariable userName : String) : List<OrderModel> = orderService.getOrdersByUserId(userName)

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

    @PostMapping("/orders")
    @Operation(
            summary = "Creates new order",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createOrder(@RequestBody order : OrderModel,
                    @Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails) = orderService.addOrder(order, user.username)

    @DeleteMapping("order/{orderId}")
    @Operation(
            summary = "Creates new order",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteOrder(@PathVariable orderId : UUID,
                    @Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails) = orderService.deleteOrder(orderId, user.username)

    @PostMapping("/order/{orderId}/pay")
    @Operation(
            summary = "Assign payment entity to order",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun assignPayment(@PathVariable orderId : UUID,
                      @RequestBody payment : PaymentModel) = orderService.assignPayment(orderId, payment)

}