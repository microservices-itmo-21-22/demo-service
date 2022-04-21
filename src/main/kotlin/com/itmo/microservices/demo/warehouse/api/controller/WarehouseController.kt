package com.itmo.microservices.demo.warehouse.api.controller

import com.itmo.microservices.demo.warehouse.api.model.Item
import com.itmo.microservices.demo.warehouse.api.service.ItemService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/items")
class WarehouseController(
    val service: ItemService
) {

    @GetMapping
    fun getItems(@RequestParam("available") available: Boolean?,
                 @RequestParam("size") size: Int?): List<Item> = service.getItems(available, size)
}
