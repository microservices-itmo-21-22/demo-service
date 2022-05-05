package com.itmo.microservices.demo.payment.impl.service

import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payment.api.service.PaymentService
import com.itmo.microservices.demo.payment.impl.repository.PaymentSubmissionRepository
import com.itmo.microservices.demo.payment.impl.repository.UserAccountFinancialLogRecordRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentServiceImpl(private val paymentSubmissionRepository: PaymentSubmissionRepository,
                         private val userAccountFinancialLogRecordRepository: UserAccountFinancialLogRecordRepository): PaymentService {
    override fun getPaymentFinLog(orderId: UUID): List<UserAccountFinancialLogRecordDto> {
        return userAccountFinancialLogRecordRepository.getUserAccountFinancialLogRecordEntitiesByOrderId(orderId)
            .map { x -> UserAccountFinancialLogRecordDto(x.type, x.amount, x.orderId, x.paymentTransactionId, x.timestamp) }
    }

    override fun orderPayment(orderId: UUID): PaymentSubmissionDto {
        val order = paymentSubmissionRepository.getByOrderByTransactionId(orderId)
        return PaymentSubmissionDto(order.timestamp!!, order.transactionId!!)
    }

}