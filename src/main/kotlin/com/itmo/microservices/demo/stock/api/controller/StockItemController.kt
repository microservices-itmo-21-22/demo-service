package com.itmo.microservices.demo.stock.api.controller

import com.itmo.microservices.demo.stock.api.model.StockItemModel
import com.itmo.microservices.demo.stock.api.service.StockItemService
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
@RequestMapping("/items")
class StockItemController(private val stockItemService: StockItemService) {

    @GetMapping
    @Operation(
        summary = "Get all stock items",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun allStockItems(): List<StockItemModel> = stockItemService.allStockItems()

    @GetMapping("/{stockItemId}")
    @Operation(
        summary = "Get stock item by id",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Stock Item not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getStockItemById(@PathVariable stockItemId: UUID): StockItemModel =
        stockItemService.getStockItemById(stockItemId)

    @PostMapping
    @Operation(
        summary = "Create stockItem",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createStockItem(@RequestBody stockItem: StockItemModel) =
        stockItemService.createStockItem(stockItem)

    @PutMapping("/{itemId}")
    @Operation(
        summary = "Change stock item",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")])
    fun changeStockItem(@PathVariable stockItemId: UUID, @RequestBody stockItem: StockItemModel) =
        stockItemService.changeStockItem(stockItemId, stockItem)

    @PutMapping("/{itemId}/add/{number}")
    @Operation(
        summary = "Add stock item",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun addStockItem(@PathVariable stockItemId: UUID, @PathVariable number: Int) =
        stockItemService.addStockItem(stockItemId, number)

    @PutMapping("/{itemId}/reserve/{number}")
    @Operation(
        summary = "Reserve stock item",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun reserveStockItem(@PathVariable stockItemId: UUID, @PathVariable number: Int) =
        stockItemService.reserveStockItem(stockItemId, number)

    @DeleteMapping("/{itemId}")
    @Operation(
        summary = "Delete stockItem",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(
                description = "Unauthorized",
                responseCode = "403",
                content = [Content()]
            )
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteStockItemById(@PathVariable stockItemId: UUID) =
        stockItemService.deleteStockItemById(stockItemId)
}
