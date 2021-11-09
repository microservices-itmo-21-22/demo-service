package com.itmo.microservices.demo.items.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.items.api.model.CatalogItem
import com.itmo.microservices.demo.items.api.service.ItemService
import com.itmo.microservices.demo.items.impl.repository.ItemRepository
import com.itmo.microservices.demo.items.impl.util.toModel
import com.itmo.microservices.demo.order.api.service.OrderService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
@Suppress("UnstableApiUsage")
class DefaultItemService(private val itemRepository: ItemRepository,
                         private val orderService: OrderService,
                         private val eventBus: EventBus
                        ) : ItemService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun getCatalogItems(): List<CatalogItem> = itemRepository.findAll()
            .map { it.toModel() }

    override fun addItemToBasket(itemId: UUID, orderId: UUID, amount: Int) {
        val item = itemRepository.findByIdOrNull(itemId)
            ?: throw NotFoundException("Item with item_id $itemId not found")

        orderService.addOrderItem(item.title, item.price, amount, orderId)
    }
}