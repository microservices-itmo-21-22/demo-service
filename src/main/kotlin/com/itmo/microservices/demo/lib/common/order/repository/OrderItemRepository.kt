package com.itmo.microservices.demo.lib.common.order.repository

import com.itmo.microservices.demo.lib.common.order.entity.OrderEntity
import com.itmo.microservices.demo.lib.common.order.entity.OrderItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderItemRepository : JpaRepository<OrderItemEntity, UUID>{
    fun findByOrderEntity(orderEntity: OrderEntity): List<OrderItemEntity>?
}
