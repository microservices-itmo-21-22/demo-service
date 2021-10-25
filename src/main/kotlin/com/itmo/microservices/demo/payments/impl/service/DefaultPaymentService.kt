package com.itmo.microservices.demo.payments.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.payments.api.messaging.PaymentProccessedEvent
import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.api.service.PaymentService
import com.itmo.microservices.demo.payments.impl.repository.PaymentRepository
import com.itmo.microservices.demo.payments.impl.util.toEntity
import com.itmo.microservices.demo.payments.impl.util.toModel
import com.itmo.microservices.demo.tasks.impl.logging.TaskServiceNotableEvents
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Suppress("UnstableApiUsage")
@Service
class DefaultPaymentService(private val paymentRepository: PaymentRepository,
                            private val eventBus: EventBus) : PaymentService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun getUserTransactionsInfo(userDetails: UserDetails): List<PaymentModel> =
        paymentRepository
            .findAllByUsername(userDetails.username)?.map { it.toModel() }
            ?: throw NotFoundException("User ${userDetails.username} not found")

    override fun refund(username: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun pay(payment: PaymentModel, userDetails: UserDetails){
        val currentDate = Date()
        val entity = payment.toEntity().also {
            it.date = currentDate
            it.username = userDetails.username
        }
        paymentRepository.save(entity)
        eventBus.post(PaymentProccessedEvent(entity.toModel()))
        eventLogger.info(
            TaskServiceNotableEvents.I_TASK_CREATED,
            entity
        )
    }
}