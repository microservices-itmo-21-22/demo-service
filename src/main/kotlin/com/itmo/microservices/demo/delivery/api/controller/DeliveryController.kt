package com.itmo.microservices.demo.delivery.api.controller

import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.tasks.api.model.TaskModel
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
@RequestMapping("/delivery")
class DeliveryController(private val deliveryService: DeliveryService) {

    @PostMapping
    @Operation(
        summary = "Create new delivery",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun doDelivery(@RequestBody request: DeliveryModel,
                   @Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails)
                        = deliveryService.doDelivery(request, user)

    @GetMapping("/{deliveryId}") //ToDo user verify
    @Operation(
        summary = "Get delivery info by ID",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Delivery not found", responseCode = "404", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getDeliveryInfo(@PathVariable deliveryId: UUID): DeliveryModel? =
        deliveryService.getDeliveryInfo(deliveryId)

    @GetMapping("/all")
    @Operation(
        summary = "Get all user deliveries",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun allDeliveries(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails)
        = deliveryService.allDeliveries(user)

    @DeleteMapping("/{deliveryId}") //ToDo user verify
    @Operation(
        summary = "Delete delivery by ID",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Delivery not found", responseCode = "404", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteDelivery(@PathVariable deliveryId: UUID) =
        deliveryService.deleteDelivery(deliveryId)
}