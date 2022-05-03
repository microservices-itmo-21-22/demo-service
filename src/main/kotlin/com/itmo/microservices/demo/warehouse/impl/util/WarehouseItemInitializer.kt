package com.itmo.microservices.demo.warehouse.impl.util

import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItemEntity
import com.itmo.microservices.demo.warehouse.impl.repository.WarehouseRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@Profile("dev")
class WarehouseItemInitializer(
        val warehouseRepository: WarehouseRepository
) {

    @PostConstruct
    fun createItems() {
        val warehouseItem1: WarehouseItemEntity = WarehouseItemEntity(
                null,
                "Example title",
                "Example description",
                100,
                100
        )
        warehouseRepository.save(warehouseItem1)
    }
}