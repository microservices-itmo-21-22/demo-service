package com.itmo.microservices.demo.items.api.controller

import com.itmo.microservices.demo.items.api.model.CatalogItem
import com.itmo.microservices.demo.items.api.service.ItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/items")
class ItemController(private val itemService: ItemService){
    @GetMapping
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
    ): List<CatalogItem> = itemService.getCatalogItems();


    @PutMapping("/orders/{order_id}/items/{item_id}")
    @Operation(
        summary = "Put item into the basket",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Can't put item into the basket", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun addItemToBasket(
        @PathVariable item_id: String,
        @PathVariable order_id: String,
        @RequestParam amount: Int,
        @Parameter(hidden = true) @AuthenticationPrincipal requester: UserDetails
    ) {

    }
}