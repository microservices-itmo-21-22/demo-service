package com.itmo.microservices.demo.order.impl.repository

import com.itmo.microservices.demo.order.impl.entities.OrderEntity
import com.itmo.microservices.demo.order.impl.entities.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, UUID>{
    fun findByOrderEntity(orderEntity: OrderEntity): List<OrderItem>?
}
