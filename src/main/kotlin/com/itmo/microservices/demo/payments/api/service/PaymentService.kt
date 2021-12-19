package com.itmo.microservices.demo.payments.api.service

import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payments.api.model.TransactionDto
import com.itmo.microservices.demo.payments.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payments.impl.entity.Transaction
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface PaymentService {
    fun pay(orderId: UUID): PaymentSubmissionDto
    fun finlog(orderId: UUID?, author: UserDetails): List<UserAccountFinancialLogRecordDto>
    fun getTransactions(): List<Transaction>
}