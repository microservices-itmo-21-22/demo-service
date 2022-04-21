package com.itmo.microservices.demo.warehouse.impl.repository

import com.itmo.microservices.demo.warehouse.impl.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrderRepository : JpaRepository<OrderEntity, UUID> {
}
