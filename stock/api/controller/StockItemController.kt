package com.itmo.microservices.demo.stock.api.controller

import com.itmo.microservices.demo.stock.api.model.StockItemModel
import com.itmo.microservices.demo.stock.api.service.StockItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/stock")
class StockItemController(private val stockItemService: StockItemService) {

    @GetMapping
    @Operation(
        summary = "Get all Stock Items",
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
            ApiResponse(description = "Stock item not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getStockItemById(@PathVariable stockItemId: UUID): StockItemModel = stockItemService.getStockItemById(stockItemId)

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
    fun addStockItem(@RequestBody stockItem: StockItemModel, @RequestBody number: Int) =
        stockItemService.addStockItem(stockItem, number)

    @PutMapping("/{stockItemId}/reserve")
    @Operation(
        summary = "Reserve stockItem",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun reserveStockItem(@RequestParam stockItemId: UUID, @RequestBody number: Int) =
        stockItemService.reserveStockItem(stockItemId, number)

    @PutMapping("/{stockItemId}/change")
    @Operation(
        summary = "Reserve stockItem",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun changeStockItem(@RequestParam stockItemId: UUID, @RequestBody stockItem: StockItemModel) =
        stockItemService.changeStockItem(stockItemId, stockItem)

    @DeleteMapping("/{stockItemId}")
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
    fun deleteStockItemById(@PathVariable stockItemId: UUID, @RequestBody number: Int) =
        stockItemService.deleteStockItemById(stockItemId, number)
}