package com.itmo.microservices.demo.payment.impl.repository

import com.itmo.microservices.demo.payment.impl.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository: JpaRepository<Payment, Int>