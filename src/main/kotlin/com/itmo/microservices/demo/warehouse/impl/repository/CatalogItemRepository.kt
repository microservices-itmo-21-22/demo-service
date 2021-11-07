package com.itmo.microservices.demo.warehouse.impl.repository

import com.itmo.microservices.demo.warehouse.impl.entity.CatalogItem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CatalogItemRepository : JpaRepository<CatalogItem?, UUID?> {
    override fun findAll(): List<CatalogItem>
    override fun existsById(id: UUID): Boolean
    fun findCatalogItemById(id: UUID?): CatalogItem?
}