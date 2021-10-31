package com.itmo.microservices.demo.tasks.api.controller

import com.itmo.microservices.demo.order.api.model.BusketModel
import com.itmo.microservices.demo.order.api.service.BusketsService
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
@RequestMapping("/orders/buskets")
class BusketsController(private val busketService: BusketsService) {

    @GetMapping
    @Operation(
        summary = "Get all buskets",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun allBuskets(): List<BusketModel> = busketService.allBuskets()

    @PostMapping
    @Operation(
        summary = "Create busket",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createBusket(@RequestBody busket: BusketModel,
                @Parameter(hidden = true) @AuthenticationPrincipal author: UserDetails) =
        busketService.createBusket(busket, author)

    @GetMapping("/{busketId}")
    @Operation(
        summary = "Get busket by id",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getBusketById(@PathVariable busketId: UUID) =
        busketService.getBusketById(busketId)

    @DeleteMapping("/{busketId}")
    @Operation(
        summary = "Delete busket by id",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteBusketById(@PathVariable busketId: UUID) =
        busketService.deleteBusketById(busketId)

    @PostMapping("/{busketId}/{productId}")
    @Operation(
        summary = "Add product to busket",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(
                description = "Unauthorized or task was not created by you",
                responseCode = "403",
                content = [Content()]
            ),
            ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun addProductToBusket(@PathVariable busketId: UUID,
                   @PathVariable productId: UUID) =
        busketService.addProductToBusket(busketId, productId)

    @DeleteMapping("/{busketId}/{productId}")
    @Operation(
        summary = "Delete product from busket",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(
                description = "Unauthorized or task was not created by you",
                responseCode = "403",
                content = [Content()]
            ),
            ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteProductFromBusket(@PathVariable busketId: UUID,
                           @PathVariable productId: UUID) =
        busketService.deleteProductFromBusket(busketId, productId)

}