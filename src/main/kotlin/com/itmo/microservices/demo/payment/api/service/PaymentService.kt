package com.itmo.microservices.demo.payment.api.service

import com.itmo.microservices.demo.payment.api.model.PaymentLogRecord
import java.util.*

interface PaymentService {
    fun getFinancialLog(orderId: UUID?): List<PaymentLogRecord>
}