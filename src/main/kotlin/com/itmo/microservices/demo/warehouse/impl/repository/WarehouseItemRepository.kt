package com.itmo.microservices.demo.warehouse.impl.repository

import org.springframework.data.jpa.repository.JpaRepository
import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItem
import java.util.UUID

interface WarehouseItemRepository : JpaRepository<WarehouseItem?, UUID?> {
    override fun existsById(uuid: UUID): Boolean
    fun findWarehouseItemById(uuid: UUID?): WarehouseItem?
}