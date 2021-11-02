package com.itmo.microservices.demo.lib.common.order.repositories

import com.itmo.microservices.demo.lib.common.order.entities.OrderEntity
import com.itmo.microservices.demo.lib.common.order.entities.OrderItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderItemRepository : JpaRepository<OrderItemEntity, UUID>{
    fun findByOrderEntity(orderEntity: OrderEntity): List<OrderItemEntity>?
}
