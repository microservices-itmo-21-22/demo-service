package com.itmo.microservices.demo.order.api.controller

import com.itmo.microservices.demo.order.api.model.OrderDto
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
class OrderController(private val orderService: OrderService) {
    @GetMapping("/{order_id}")
    @Operation(
        summary = "Get order",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getOrder(
        @PathVariable order_id: UUID,
        @Parameter(hidden = true) @AuthenticationPrincipal requester: UserDetails
    ): OrderDto = orderService.getOrder(order_id)

    @PostMapping
    @Operation(
        summary = "Create new order",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createOrder(@AuthenticationPrincipal user : UserDetails) : OrderDto = orderService.createOrder(user)

    @GetMapping("/submit/{order_id}")
    @Operation(
        summary = "Submit order",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun submitOrder(
        @PathVariable order_id: UUID,
        @Parameter(hidden = true)
        @AuthenticationPrincipal user : UserDetails
    ): OrderDto = orderService.submitOrder(user, order_id)
}
