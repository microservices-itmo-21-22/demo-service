package com.itmo.microservices.demo.warehouse.api.controller

import com.itmo.microservices.demo.warehouse.api.model.CatalogItemDto
import com.itmo.microservices.demo.warehouse.impl.service.WarehouseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/items")
class WarehouseController (
        val warehouseService: WarehouseService
) {

    @GetMapping
    @Operation(
            summary = "Get warehouse items",
            responses = [
                ApiResponse(description = "OK", responseCode = "200")
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getItems(@RequestParam available: Boolean, @RequestParam size: Int): List<CatalogItemDto> {
        return warehouseService.getItems(available, size)
    }
}