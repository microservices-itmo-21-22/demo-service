package com.itmo.microservices.demo.payments.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.payments.api.messaging.PaymentProccessedEvent
import com.itmo.microservices.demo.payments.api.model.FinancialOperationType
import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payments.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payments.api.service.PaymentService
import com.itmo.microservices.demo.payments.impl.entity.Payment
import com.itmo.microservices.demo.payments.impl.entity.PaymentsOrderEntity
import com.itmo.microservices.demo.payments.impl.entity.UserAccountFinancialLogRecordEntity
import com.itmo.microservices.demo.payments.impl.repository.OrderPaymentsRepository
import com.itmo.microservices.demo.payments.impl.repository.PaymentRepository
import com.itmo.microservices.demo.payments.impl.repository.UserAccountFinancialLogRecordRepository
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
                            private val orderRepository: OrderPaymentsRepository,
                            private val userAccountFinancialLogRecordRepository: UserAccountFinancialLogRecordRepository) : PaymentService {

    private fun createOrderMock() = PaymentsOrderEntity(
            "string",
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
        val amount = order.itemsMap?.map{ (id, amount) -> amount}?.sumOf { it.amount ?: 0 }
        val record = UserAccountFinancialLogRecordEntity(
                UUID.randomUUID(),
                FinancialOperationType.WITHDRAW,
                amount,
                order.id,
                Date().time
        )
        userAccountFinancialLogRecordRepository.save(record)
        return PaymentSubmissionDto(UUID.randomUUID(), Date().time)
    }

    override fun finlog(orderId: UUID?, author: UserDetails): List<UserAccountFinancialLogRecordDto> {
        val mock = createOrderMock()
        orderRepository.save(mock)
        print(mock.id)
        orderId?.let {
            val infoList = userAccountFinancialLogRecordRepository.findAllByOrderId(orderId)
            return infoList.map { it.toModel() }
        }
        val orders = orderRepository.findAllByUsername(author.username)
        val infoList = orders.mapNotNull { it.id }.flatMap { userAccountFinancialLogRecordRepository.findAllByOrderId(it) }
        return infoList.map { it.toModel() }
    }
}