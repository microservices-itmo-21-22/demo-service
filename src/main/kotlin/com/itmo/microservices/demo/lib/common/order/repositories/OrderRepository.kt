package com.itmo.microservices.demo.lib.common.order.repositories

import com.itmo.microservices.demo.lib.common.order.entities.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderRepository : JpaRepository<OrderEntity, UUID>
