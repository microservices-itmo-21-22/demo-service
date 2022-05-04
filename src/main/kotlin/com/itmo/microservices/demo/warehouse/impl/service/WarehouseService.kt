package com.itmo.microservices.demo.warehouse.impl.service

import com.itmo.microservices.demo.warehouse.api.model.CatalogItemDto
import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItemEntity
import com.itmo.microservices.demo.warehouse.impl.repository.WarehouseRepository
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class WarehouseService(
        val warehouseRepository: WarehouseRepository
) {

    fun getItems(available: Boolean, size: Int): List<CatalogItemDto> {
        val items: List<WarehouseItemEntity> =
                if (available)
                    warehouseRepository.findAvailableItems(size)
                else
                    warehouseRepository.findAllItems(size)
        return items.stream()
                .map { item -> item.toModel() }
                .collect(Collectors.toList())
    }

    fun WarehouseItemEntity.toModel(): CatalogItemDto {
        return CatalogItemDto(
                this.id,
                this.title,
                this.description,
                this.price,
                this.amount
        )
    }
}
