package com.itmo.microservices.demo.orders.api.controller

import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.orders.api.service.OrderService
import com.itmo.microservices.demo.shoppingCartService.api.service.CartService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*

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
    fun createOrder(@AuthenticationPrincipal user: UserDetails) = shoppingCartService.makeCart()

    @PutMapping("/orders/{order_id}/items/{item_id}")
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
    fun putItemsToCart(@PathVariable order_id : UUID, @PathVariable item_id : UUID, @RequestParam(value = "amount") amount : Int = 1, @AuthenticationPrincipal user : UserDetails) = shoppingCartService.putItemInCart(order_id, item_id, amount)

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
    fun book(@PathVariable order_id : UUID, @AuthenticationPrincipal user : UserDetails) = orderService.createOrderFromBusket(order_id, user)

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
    fun deliver(@RequestBody deliveryModel: DeliveryModel) = deliveryService.addDelivery(deliveryModel)

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
    fun getOrder(@PathVariable order_id: UUID) = shoppingCartService.getCart(order_id)

}