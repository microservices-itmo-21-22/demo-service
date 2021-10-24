package com.itmo.microservices.demo.payment.impl.repository

import com.itmo.microservices.demo.payment.impl.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository: JpaRepository<Payment, Int> {
}