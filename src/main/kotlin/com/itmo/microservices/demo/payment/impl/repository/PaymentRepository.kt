package com.itmo.microservices.demo.payment.impl.repository

import com.itmo.microservices.demo.payment.impl.entity.PaymentLogRecordEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentRepository: JpaRepository<PaymentLogRecordEntity, UUID> {

    fun findAllByOrderId(orderId: UUID?): List<PaymentLogRecordEntity>

}
