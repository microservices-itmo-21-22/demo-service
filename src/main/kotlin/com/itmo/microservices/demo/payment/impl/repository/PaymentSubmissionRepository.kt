package com.itmo.microservices.demo.payment.impl.repository

import com.itmo.microservices.demo.payment.impl.entity.PaymentSubmissionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PaymentSubmissionRepository: JpaRepository<PaymentSubmissionEntity, Long> {
    fun getByOrderByTransactionId(order_id: UUID): PaymentSubmissionEntity
}
