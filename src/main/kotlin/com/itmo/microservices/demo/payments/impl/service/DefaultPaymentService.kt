package com.itmo.microservices.demo.payments.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.payments.api.messaging.PaymentProccessedEvent
import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.api.service.PaymentService
import com.itmo.microservices.demo.payments.impl.entity.Payment
import com.itmo.microservices.demo.payments.impl.repository.PaymentRepository
import com.itmo.microservices.demo.payments.impl.util.toEntity
import com.itmo.microservices.demo.payments.impl.util.toModel
import com.itmo.microservices.demo.tasks.api.messaging.TaskAssignedEvent
import com.itmo.microservices.demo.tasks.impl.logging.TaskServiceNotableEvents
import com.itmo.microservices.demo.tasks.impl.util.toModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Suppress("UnstableApiUsage")
@Service
class DefaultPaymentService(private val paymentRepository: PaymentRepository) : PaymentService {

    override fun getUserTransactionsInfo(userDetails: UserDetails): List<PaymentModel> =
        paymentRepository
            .findAllByUsername(userDetails.username)?.map { it.toModel() }
            ?: throw NotFoundException("User ${userDetails.username} not found")

    override fun refund(paymentId: UUID, userDetails: UserDetails){
        val payment = paymentRepository.findByIdOrNull(paymentId) ?: throw NotFoundException("Payment $paymentId not found")
        payment.status = 1
        paymentRepository.save(payment)
    }

    override fun pay(userDetails: UserDetails): PaymentModel{
        val currentDate = Date()
        val entity = Payment().also {
            it.date = currentDate
            it.username = userDetails.username
        }
        paymentRepository.save(entity)
        return entity.toModel()
    }
}