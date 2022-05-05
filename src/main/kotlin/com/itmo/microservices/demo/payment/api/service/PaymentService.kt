package com.itmo.microservices.demo.payment.api.service

import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import java.util.UUID

interface PaymentService {
    fun getPaymentFinLog(orderId: UUID): List<UserAccountFinancialLogRecordDto>

    fun orderPayment(orderId: UUID): PaymentSubmissionDto
}