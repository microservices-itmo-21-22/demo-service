package com.itmo.microservices.demo.warehouse.impl.repository

import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface WarehouseRepository : JpaRepository<WarehouseItemEntity, UUID> {
    @Query("SELECT * FROM warehouse_item WHERE amount > 0 LIMIT :size", nativeQuery = true)
    fun findAvailableItems(size: Int): List<WarehouseItemEntity>
    @Query("SELECT * FROM warehouse_item LIMIT :size", nativeQuery = true)
    fun findAllItems(size: Int): List<WarehouseItemEntity>
}