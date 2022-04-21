package com.itmo.microservices.demo.warehouse.impl.service

import com.itmo.microservices.demo.warehouse.api.model.Item
import com.itmo.microservices.demo.warehouse.api.service.ItemService
import com.itmo.microservices.demo.warehouse.impl.repository.ItemRepository
import com.itmo.microservices.demo.warehouse.impl.util.toModel
import org.springframework.stereotype.Service

@Service
class DefaultItemService(
    private val itemRepository: ItemRepository
) : ItemService {

    override fun getItems(available: Boolean?, size: Int?): List<Item> = itemRepository.findAll()
        .map { it.toModel() }

}
