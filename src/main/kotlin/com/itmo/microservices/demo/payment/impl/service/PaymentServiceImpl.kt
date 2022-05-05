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
    override fun getPaymentFinLog(order_id: UUID): List<UserAccountFinancialLogRecordDto> {
        return userAccountFinancialLogRecordRepository.getUserAccountFinancialLogRecordEntitiesByOrderId(order_id)
            .map { x -> UserAccountFinancialLogRecordDto(x.type, x.amount, x.orderId, x.paymentTransactionId, x.timestamp) }
    }

    override fun orderPayment(order_id: UUID): PaymentSubmissionDto {
        val order = paymentSubmissionRepository.getByOrderByTransactionId(order_id)
        return PaymentSubmissionDto(order.timestamp!!, order.transactionId!!)
    }

}