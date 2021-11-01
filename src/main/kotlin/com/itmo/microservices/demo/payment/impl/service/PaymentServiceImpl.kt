package com.itmo.microservices.demo.payment.impl.service

import com.itmo.microservices.demo.payment.api.model.PaymentModel
import com.itmo.microservices.demo.payment.api.model.PaymentStatus
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.service.PaymentService
import com.itmo.microservices.demo.payment.impl.entity.Payment
import com.itmo.microservices.demo.payment.impl.repository.PaymentRepository
import org.springframework.stereotype.Service

@Service
class PaymentServiceImpl(private val paymentRepository: PaymentRepository): PaymentService {

    override fun executePayment(paymentModel: PaymentModel): PaymentSubmissionDto {
        paymentRepository.save(paymentModel.toEntity())

        return PaymentSubmissionDto(System.currentTimeMillis(), 0)
    }

    private fun PaymentModel.toEntity() = Payment(0, PaymentStatus.SUCCESS, 0)
}