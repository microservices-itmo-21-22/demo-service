package com.itmo.microservices.demo.order.impl.repository

import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderRepository : JpaRepository<OrderEntity, UUID>