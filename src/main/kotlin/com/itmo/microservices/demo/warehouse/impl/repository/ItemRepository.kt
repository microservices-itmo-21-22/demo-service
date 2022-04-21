package com.itmo.microservices.demo.warehouse.impl.repository

import com.itmo.microservices.demo.warehouse.impl.entity.ItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ItemRepository : JpaRepository<ItemEntity, UUID> {
}
