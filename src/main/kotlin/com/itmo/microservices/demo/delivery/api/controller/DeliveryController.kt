package com.itmo.microservices.demo.delivery.api.controller

import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.order.api.model.BookingDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*


@RestController
@RequestMapping("/delivery")
class DeliveryController (private val deliveryService: DeliveryService) {
    @GetMapping("/slots")
    @Operation(
        summary = "Get available delivery slots",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getAvailableDeliverySlots(
        @RequestParam("number") number: Int
    ): List<LocalDateTime> = deliveryService.getAvailableDeliverySlots(number)

    @PostMapping("/{order_id}/time")
    @Operation(
        summary = "Set desired delivery time",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun setDesiredDeliveryTime(
        @PathVariable order_id: UUID,
        @RequestParam("slot") slot_in_sec: Int
    ): BookingDto = deliveryService.setDesiredDeliveryTime(order_id, slot_in_sec)
}