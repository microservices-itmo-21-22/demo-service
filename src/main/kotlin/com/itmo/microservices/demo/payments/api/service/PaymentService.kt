package com.itmo.microservices.demo.payments.api.service

import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.impl.entity.Payment
import com.itmo.microservices.demo.payments.impl.entity.PaymentAppUser
import org.springframework.security.core.userdetails.UserDetails

interface PaymentService {
    fun getUserTransactionsInfo(userDetails: UserDetails): List<PaymentModel>
    fun refund(username: String): Boolean
    fun pay(payment: PaymentModel, userDetails: UserDetails)
}