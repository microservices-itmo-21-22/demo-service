package com.itmo.microservices.demo.tasks.impl.repository

import com.itmo.microservices.demo.order.impl.entities.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderRepository : JpaRepository<OrderEntity, UUID>
