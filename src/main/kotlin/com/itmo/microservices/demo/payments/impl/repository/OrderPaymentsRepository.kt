package com.itmo.microservices.demo.payments.impl.repository

import com.itmo.microservices.demo.payments.impl.entity.PaymentsOrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderPaymentsRepository : JpaRepository<PaymentsOrderEntity, UUID> {
    fun findAllByUsername(username: String): List<PaymentsOrderEntity>
}