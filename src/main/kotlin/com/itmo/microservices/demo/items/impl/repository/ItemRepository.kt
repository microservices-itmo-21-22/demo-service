package com.itmo.microservices.demo.items.impl.repository

import com.itmo.microservices.demo.items.impl.entity.CatalogItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ItemRepository : JpaRepository<CatalogItemEntity, UUID> {
}