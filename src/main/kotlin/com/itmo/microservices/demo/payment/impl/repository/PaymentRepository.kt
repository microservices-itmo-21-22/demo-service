package com.itmo.microservices.demo.payment.impl.repository

import com.itmo.microservices.demo.payment.impl.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentRepository : JpaRepository<Payment, UUID> {
    fun findByOrderId(orderId: UUID): List<Payment>
}