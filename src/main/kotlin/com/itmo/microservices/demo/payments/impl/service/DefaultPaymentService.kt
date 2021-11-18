package com.itmo.microservices.demo.payments.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.payments.api.messaging.PaymentProccessedEvent
import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payments.api.service.PaymentService
import com.itmo.microservices.demo.payments.impl.entity.OrderEntity
import com.itmo.microservices.demo.payments.impl.entity.Payment
import com.itmo.microservices.demo.payments.impl.repository.OrderRepository
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
class DefaultPaymentService(private val paymentRepository: PaymentRepository,
                            private val orderRepository: OrderRepository) : PaymentService {

    private fun createOrderMock() = OrderEntity(
            null,
            Date().time,
            OrderStatus.COMPLETED,
            mutableMapOf(),
            10,
            listOf()
    )

    override fun getUserTransactionsInfo(userDetails: UserDetails): List<PaymentModel> =
        paymentRepository
            .findAllByUsername(userDetails.username)?.map { it.toModel() }
            ?: throw NotFoundException("User ${userDetails.username} not found")

    override fun refund(paymentId: UUID, userDetails: UserDetails){
        val payment = paymentRepository.findByIdOrNull(paymentId) ?: throw NotFoundException("Payment $paymentId not found")
        payment.status = 1
        paymentRepository.save(payment)
    }

    override fun pay(orderId: UUID): PaymentSubmissionDto {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        order.status = OrderStatus.PAID
        orderRepository.save(order)
        return PaymentSubmissionDto(UUID.randomUUID(), Date().time)
    }

}