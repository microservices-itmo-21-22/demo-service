package com.itmo.microservices.demo.payment.api.service

import com.itmo.microservices.demo.payment.api.model.PaymentModel

interface PaymentService {
    fun executePayment(paymentModel: PaymentModel)
}