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
class OrderController(private var orderService: OrderService) {
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
    fun createOrder(@Parameter(hidden = true) @AuthenticationPrincipal author: UserDetails) =
        orderService.createOrder(author)

    @PutMapping("/{orderId}/items/{itemId}?amount={amount}")
    @Operation(
            summary = "Добавление товара в корзину",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun addItemToOrder(@PathVariable orderId: UUID, @PathVariable itemId: UUID, @PathVariable amount: Int,
                       @Parameter(hidden = true) @AuthenticationPrincipal author: UserDetails) =
            orderService.addItemToOrder(orderId, itemId, amount)

    @PostMapping("{orderId}/bookings")
    @Operation(
            summary = "Оформление (финализация/бронирование) заказа",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun registerOrder(@PathVariable orderId: UUID,
                      @Parameter(hidden = true) @AuthenticationPrincipal author: UserDetails) =
        orderService.registerOrder(orderId)

    @PostMapping("/orders/{orderId}/delivery")
    @Operation(
            summary = "Установление желаемого времени доставки",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun setDeliveryTime(@PathVariable orderId: UUID, @RequestParam slot: Int) {
        orderService.setDeliveryTime(orderId, slot)
    }
}