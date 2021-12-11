package com.itmo.microservices.demo.payment.api.service

import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import java.util.*

interface PaymentService {
    fun getFinLog(orderId: UUID) : List<UserAccountFinancialLogRecordDto>
    fun makePayment(orderId: UUID) : PaymentSubmissionDto
}