package com.itmo.microservices.demo.warehouse.api.service

import com.itmo.microservices.demo.warehouse.api.model.Item

interface ItemService {
    fun getItems(available: Boolean?, size: Int?): List<Item>
}
