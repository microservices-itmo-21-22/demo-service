package com.itmo.microservices.demo.order.impl.repository

import com.itmo.microservices.demo.order.impl.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ItemRepository : JpaRepository<OrderItem, UUID> {}