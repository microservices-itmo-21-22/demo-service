package com.itmo.microservices.demo.payments.api.service

import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.impl.entity.Payment
import com.itmo.microservices.demo.payments.impl.entity.PaymentAppUser
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface PaymentService {
    fun getUserTransactionsInfo(userDetails: UserDetails): List<PaymentModel>
    fun refund(paymentId: UUID, userDetails: UserDetails)
    fun pay(userDetails: UserDetails): PaymentModel
}