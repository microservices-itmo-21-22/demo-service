package com.itmo.microservices.demo.lib.common.order.repository

import com.itmo.microservices.demo.lib.common.order.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderRepository : JpaRepository<OrderEntity, UUID>
