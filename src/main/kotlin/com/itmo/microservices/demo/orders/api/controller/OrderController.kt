package com.itmo.microservices.demo.orders.api.controller

import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.orders.api.model.OrderModel
import com.itmo.microservices.demo.orders.api.model.OrderModelDTO
import com.itmo.microservices.demo.orders.api.model.OrderStatus
import com.itmo.microservices.demo.orders.api.service.OrderService
import com.itmo.microservices.demo.orders.impl.entity.Order
import com.itmo.microservices.demo.orders.impl.util.toEntity
import com.itmo.microservices.demo.shoppingCartService.api.model.ShoppingCartDTO
import com.itmo.microservices.demo.shoppingCartService.api.service.CartService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.HashMap

@RestController
class OrderController(private val orderService: OrderService,
                      private val shoppingCartService: CartService,
                      private val deliveryService: DeliveryService) {

    @PostMapping("/orders")
    @Operation(
            summary = "Creates new order",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
                ApiResponse(description = "Service error", responseCode = "500", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createOrder(@AuthenticationPrincipal user: UserDetails): OrderModelDTO {
        var cart = shoppingCartService.makeCart() ?: throw NullPointerException("Cart service failed to create cart")
        var order = orderService.createOrderFromBusket(cart.id, user)
        return convertOrderAndCartToDTO(order.toEntity(), cart)
    }

    @PutMapping("/orders/{order_id}/items/{item_id}?amount={amount}")
    @Operation(
            summary = "Put items to cart",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
                ApiResponse(description = "Service error", responseCode = "500", content = [Content()]),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun putItemsToCart(@PathVariable order_id : UUID, @PathVariable item_id : UUID, @PathVariable amount : Int, @AuthenticationPrincipal user : UserDetails) = shoppingCartService.putItemInCart(order_id, item_id, amount)

    @DeleteMapping("/orders/{order_id}/bookings")
    @Operation(
            summary = "Finalization and booking",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
                ApiResponse(description = "Service error", responseCode = "500", content = [Content()]),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun book(@PathVariable order_id : UUID, @AuthenticationPrincipal user : UserDetails): Nothing = throw NotImplementedError();

    @PostMapping("/orders/{order_id}/delivery?slot={slot_in_sec}")
    @Operation(
            summary = "Choosing desired slot",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
                ApiResponse(description = "Service error", responseCode = "500", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deliver(@PathVariable orderId : UUID, @PathVariable slotInSec : Int) = deliveryService.reserveDeliverySlots(orderId, slotInSec)

    @GetMapping("/orders/{order_id}")
    @Operation(
        summary = "Returns current order",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Service error", responseCode = "500", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getOrder(@PathVariable order_id: UUID) : OrderModelDTO {
        var cart = shoppingCartService.getCart(order_id) ?: throw NotFoundException()
        var order = orderService.getOrder(order_id).toEntity()
        return convertOrderAndCartToDTO(order, cart)
    }

    fun convertOrderAndCartToDTO(order : Order, cart : ShoppingCartDTO) : OrderModelDTO {
        val items = HashMap<UUID, Int>()
        for(i in cart.items) {
            i.amount?.let { items.put(i.id, it) }
        }
        return OrderModelDTO(order.id, System.currentTimeMillis(), order.status, items, null, arrayListOf());
    }

}