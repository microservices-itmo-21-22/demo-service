package com.itmo.microservices.demo.items.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.items.api.model.CatalogItem
import com.itmo.microservices.demo.items.api.service.WarehouseService
import com.itmo.microservices.demo.items.impl.util.toEntity
import com.itmo.microservices.demo.lib.common.items.repository.ItemRepository
import com.itmo.microservices.demo.items.impl.util.toModel
import java.util.UUID
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
@Suppress("UnstableApiUsage")
class DefaultWarehouseService(private val itemRepository: ItemRepository,
                              private val eventBus: EventBus
                        ) : WarehouseService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun getCatalogItems(available: Boolean): List<CatalogItem> =
        itemRepository.findAll()
        .filter { if (available) it.amount > 0 else true }
        .map { it.toModel() }

    override fun getItem(itemId: UUID): CatalogItem?
        = itemRepository.findByIdOrNull(itemId)?.toModel();

    override fun addItem(catalogItem: CatalogItem) {
       itemRepository.save(catalogItem.toEntity());
    }
}
