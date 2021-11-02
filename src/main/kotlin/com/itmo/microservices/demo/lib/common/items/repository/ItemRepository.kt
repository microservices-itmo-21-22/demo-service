package com.itmo.microservices.demo.lib.common.items.repository

import com.itmo.microservices.demo.lib.common.items.entity.CatalogItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ItemRepository : JpaRepository<CatalogItemEntity, UUID> {
}
