package com.itmo.microservices.demo.warehouse.impl.repository

import com.itmo.microservices.demo.warehouse.impl.entity.WCatalogItem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ICatalogItemRepository : JpaRepository<WCatalogItem?, UUID?> {
    override fun findAll(): List<WCatalogItem>
    override fun existsById(id: UUID): Boolean
    fun findCatalogItemById(id: UUID?): WCatalogItem?
}