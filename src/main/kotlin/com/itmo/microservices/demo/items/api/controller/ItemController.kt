package com.itmo.microservices.demo.items.api.controller

import com.itmo.microservices.demo.items.api.model.CatalogItemDto
import com.itmo.microservices.demo.items.api.service.WarehouseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
class ItemController(private val itemService: WarehouseService){
    @GetMapping("/items")
    @Operation(
        summary = "Get all items",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun allItems(
        @RequestParam available: Boolean,
        @Parameter(hidden = true) @AuthenticationPrincipal requester: UserDetails
    ): List<CatalogItemDto> = itemService.getCatalogItems(available);

    @PostMapping("/_internal/catalogItem")
    @Operation(
        summary = "Add new item",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun addNewItem(
        @RequestBody catalogItemDto: CatalogItemDto,
        @Parameter(hidden = true) @AuthenticationPrincipal requester: UserDetails
    ) = itemService.addItem(catalogItemDto)
}