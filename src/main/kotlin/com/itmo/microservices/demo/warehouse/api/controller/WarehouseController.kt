package com.itmo.microservices.demo.warehouse.api.controller

import com.itmo.microservices.demo.warehouse.impl.service.WarehouseService
import com.itmo.microservices.demo.warehouse.api.model.ItemQuantityChangeRequest
import com.itmo.microservices.demo.warehouse.impl.entity.WCatalogItem
import org.springframework.http.ResponseEntity
import java.util.UUID
import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItem
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/warehouse")
@SecurityRequirement(name = "bearerAuth")
class WarehouseController(private val service: WarehouseService) {
    @PostMapping(value = ["/income"], consumes = ["application/json"], produces = ["application/json"])
    fun income(@RequestBody values: ItemQuantityChangeRequest?): ResponseEntity<String> {
        return service.income(values!!)
    }

    @PostMapping(value = ["/outcome"], consumes = ["application/json"], produces = ["application/json"])
    fun outcome(@RequestBody values: ItemQuantityChangeRequest?): ResponseEntity<String> {
        return service.outcome(values!!)
    }

    @PostMapping(value = ["/book"], consumes = ["application/json"], produces = ["application/json"])
    fun book(@RequestBody values: ItemQuantityChangeRequest?): ResponseEntity<String> {
        return service.book(values!!)
    }

    @PostMapping(value = ["/unbook"], consumes = ["application/json"], produces = ["application/json"])
    fun unbook(@RequestBody values: ItemQuantityChangeRequest?): ResponseEntity<String> {
        return service.unbook(values!!)
    }

    @PostMapping(value = ["/addItem"], consumes = ["application/json"], produces = ["application/json"])
    fun addItem(@RequestBody item: WCatalogItem?): ResponseEntity<String> {
        return service.addItem(item!!)
    }

    @get:GetMapping(value = ["/getItems"])
    val itemsList: ResponseEntity<List<WCatalogItem>>
        get() = service.itemsList

    @GetMapping(value = ["/getItem"])
    fun getItem(@RequestParam("id") id: UUID?): ResponseEntity<WCatalogItem> {
        return service.getItem(id)
    }

    @GetMapping(value = ["/getItemQuantity"])
    fun getItemQuantity(@RequestParam("id") id: UUID?): ResponseEntity<WarehouseItem> {
        return service.getItemQuantity(id)
    }
}