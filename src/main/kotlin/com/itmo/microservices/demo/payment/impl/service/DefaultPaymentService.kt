package com.itmo.microservices.demo.payment.impl.service

import com.itmo.microservices.demo.payment.api.model.PaymentLogRecord
import com.itmo.microservices.demo.payment.api.service.PaymentService
import com.itmo.microservices.demo.payment.impl.repository.PaymentRepository
import com.itmo.microservices.demo.payment.impl.util.toModel
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultPaymentService (
    private val paymentRepository: PaymentRepository
) : PaymentService {

    override fun getFinancialLog(orderId: UUID?): List<PaymentLogRecord> {
        if (orderId != null) {
            return paymentRepository.findAllByOrderId(orderId).map { it.toModel() }
        }
        return paymentRepository.findAll().map { it.toModel() }
    }

}
