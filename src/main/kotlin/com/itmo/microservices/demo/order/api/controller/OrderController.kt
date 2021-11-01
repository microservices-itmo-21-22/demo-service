package com.itmo.microservices.demo.order.api.controller

import com.itmo.microservices.demo.order.api.dto.Order
import com.itmo.microservices.demo.order.impl.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/orders")
class OrderController @Autowired constructor(private val service: OrderService) {
    @PostMapping
    @Operation(
        summary = "Create order",
        responses = [ApiResponse(
            description = "OK",
            responseCode = "200",
            content = [Content()]
        ), ApiResponse(description = "Something went wrong", responseCode = "400", content = [Content()])],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createOrder(@RequestBody order: Order?) {
        service.createOrder(order)
    }

    @GetMapping("/{order_id}")
    @Operation(
        summary = "Get order",
        responses = [ApiResponse(
            description = "OK",
            responseCode = "200",
            content = [Content()]
        ), ApiResponse(description = "Something went wrong", responseCode = "400", content = [Content()])],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getOrder(@PathVariable("order_id") uuid: UUID?) {
        service.getOrderById(uuid)
    }

    @PutMapping("/{order_id}/items/{item_id}?amount={amount}")
    @Operation(
        summary = "Put the item in the cart",
        responses = [ApiResponse(
            description = "OK",
            responseCode = "200"
        ), ApiResponse(description = "Something went wrong", responseCode = "400")],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun updateOrder(
        @PathVariable("order_id") orderId: UUID?,
        @PathVariable("item_id") itemId: UUID?,
        @PathVariable("amount") amount: Int
    ) {
        service.updateOrder(orderId, itemId, amount)
    }

    @PostMapping("/{order_id}/bookings")
    @Operation(
        summary = "Book",
        responses = [ApiResponse(
            description = "OK",
            responseCode = "200",
            content = [Content()]
        ), ApiResponse(description = "Something went wrong", responseCode = "400", content = [Content()])],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun bookOrder(@PathVariable("order_id") orderId: UUID?) {
        service.book(orderId)
    }

    @PostMapping("/{order_id}/delivery?slot={slot_in_sec}")
    @Operation(
        summary = "Deliveru time selection",
        responses = [ApiResponse(
            description = "OK",
            responseCode = "200"
        ), ApiResponse(description = "Something went wrong", responseCode = "400")],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun selectDeliveryTime(
        @PathVariable("order_id") orderId: UUID?,
        @PathVariable("slot_in_sec") seconds: Int
    ) {
        service.selectDeliveryTime(orderId, seconds)
    }
}