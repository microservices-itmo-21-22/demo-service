package com.itmo.microservices.demo.shoppingCartService.impl.repository

import com.itmo.microservices.demo.shoppingCartService.impl.entity.CatalogItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CatalogItemRepository : JpaRepository<CatalogItem, UUID> {
}