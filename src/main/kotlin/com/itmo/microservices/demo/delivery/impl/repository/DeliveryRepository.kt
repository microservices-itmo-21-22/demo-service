package com.itmo.microservices.demo.delivery.impl.repository

import com.itmo.microservices.demo.delivery.impl.entity.Delivery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DeliveryRepository : JpaRepository<Delivery, UUID>, JpaSpecificationExecutor<Delivery> {

    @Query("FROM Delivery WHERE orderId = ?1")
    fun findByOrderId(orderId: UUID): List<Delivery>
}