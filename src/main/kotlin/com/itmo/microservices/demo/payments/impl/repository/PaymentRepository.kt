package com.itmo.microservices.demo.payments.impl.repository

import com.itmo.microservices.demo.payments.impl.entity.Payment
import com.itmo.microservices.demo.payments.impl.entity.PaymentAppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.lang.Nullable
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository: JpaRepository<Payment, String>{
    fun findAllByUsername(username: String): List<Payment>?
}