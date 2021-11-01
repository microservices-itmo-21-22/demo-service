package com.itmo.microservices.demo.payments.impl.repository

import com.itmo.microservices.demo.payments.impl.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PaymentRepository: JpaRepository<Payment, UUID>{
    fun findAllByUsername(username: String): List<Payment>?
}