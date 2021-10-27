package com.itmo.microservices.demo.order.impl.repository

import com.itmo.microservices.demo.order.impl.entities.Order
import com.itmo.microservices.demo.order.impl.entities.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, UUID>{
    fun findByOrder(order: Order): List<OrderItem>?
}