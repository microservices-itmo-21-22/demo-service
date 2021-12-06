package com.itmo.microservices.demo.stock.api.controller

import com.itmo.microservices.demo.stock.api.model.StockItemModel
import com.itmo.microservices.demo.stock.api.service.StockItemService
import com.itmo.microservices.demo.tasks.api.model.TaskModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpServerErrorException
import java.util.*

@RestController
@RequestMapping
class StockItemController(private val stockItemService: StockItemService) {

    @GetMapping("/items?available={available}")
    @Operation(
        summary = "Get all stock items",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun allStockItems(@PathVariable available : Boolean): List<StockItemModel> = stockItemService.allStockItems()

    @GetMapping("/items/{itemId}")
    @Operation(
        summary = "Get stock item by id",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Stock Item not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getStockItemById(@PathVariable itemId: UUID): StockItemModel =
        stockItemService.getStockItemById(itemId)

    @PostMapping
    @Operation(
        summary = "Create stockItem",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Incorrect input", responseCode = "405", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createStockItem(@RequestBody stockItem: StockItemModel) {
        if (!stockItemService.createStockItem(stockItem)) {
            throw HttpServerErrorException(HttpStatus.METHOD_NOT_ALLOWED, "Cannot create") //405
        }
    }

    @PutMapping("/items/{itemId}")
    @Operation(
        summary = "Change stock item",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")])
    fun changeStockItem(@PathVariable itemId: UUID, @RequestBody stockItem: StockItemModel
    ) =
        stockItemService.changeStockItem(itemId, stockItem)

    @PutMapping("/items/{itemId}/add/{number}")
    @Operation(
        summary = "Add stock item",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun addStockItem(@PathVariable itemId: UUID, @PathVariable number: Int) =
        stockItemService.addStockItem(itemId, number)

    @PutMapping("/{itemId}/reserve/{number}")
    @Operation(
        summary = "Reserve stock item",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Incorrect input", responseCode = "405", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun reserveStockItem(@PathVariable itemId: UUID, @PathVariable number: Int) {
        if (!stockItemService.reserveStockItem(itemId, number)) {
            throw HttpServerErrorException(HttpStatus.METHOD_NOT_ALLOWED, "Cannot reserve") //405
        }
    }


    @DeleteMapping("/items/{itemId}")
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
    fun deleteStockItemById(@PathVariable itemId: UUID) =
        stockItemService.deleteStockItemById(itemId)
}
