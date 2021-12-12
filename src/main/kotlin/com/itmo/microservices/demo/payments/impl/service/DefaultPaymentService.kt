package com.itmo.microservices.demo.payments.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.payments.api.model.FinancialOperationType
import com.itmo.microservices.demo.payments.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payments.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payments.api.service.PaymentService
import com.itmo.microservices.demo.payments.impl.entity.UserAccountFinancialLogRecordEntity
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import com.itmo.microservices.demo.order.impl.service.OrderServiceImpl
import com.itmo.microservices.demo.payments.impl.logging.PaymentServiceNotableEvents
import com.itmo.microservices.demo.payments.impl.repository.PaymentRepository
import com.itmo.microservices.demo.payments.impl.repository.TransactionRepository
import com.itmo.microservices.demo.payments.impl.repository.UserAccountFinancialLogRecordRepository
import com.itmo.microservices.demo.payments.impl.util.toEntity
import com.itmo.microservices.demo.payments.impl.util.toModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

import java.util.*

@Suppress("UnstableApiUsage")
@Service
class DefaultPaymentService(private val paymentRepository: PaymentRepository,
                            private val transactionRepository: TransactionRepository,
                            private val transactionRequestService: TransactionRequestService,
                            private val orderRepository: OrderRepository,
                            private val userAccountFinancialLogRecordRepository: UserAccountFinancialLogRecordRepository,
                            private val eventBus: EventBus) : PaymentService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(OrderServiceImpl::class.java)
    }

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

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

        var transactionResponse = transactionRequestService.postRequest()
        transactionResponse = transactionRequestService.poll(transactionResponse.id)

        val paymentSubmission = PaymentSubmissionDto(transactionResponse.id, Date().time)
        eventBus.post(paymentSubmission)
        eventLogger.info(
                PaymentServiceNotableEvents.I_ORDER_PAID,
                paymentSubmission
        )

        transactionRepository.save(transactionResponse.toEntity())
        return paymentSubmission
    }

    override fun finlog(orderId: UUID?, author: UserDetails): List<UserAccountFinancialLogRecordDto> {
        orderId?.let {
            val infoList = userAccountFinancialLogRecordRepository.findAllByOrderId(orderId)
            eventLogger.info(
                    PaymentServiceNotableEvents.I_PAYMENT_GOT,
                    infoList
            )
            return infoList.map { it.toModel() }
        }
        val orders = orderRepository.findAllByUsername(author.username)
        val infoList = orders.mapNotNull { it.id }.flatMap { userAccountFinancialLogRecordRepository.findAllByOrderId(it) }
        eventLogger.info(
                PaymentServiceNotableEvents.I_PAYMENTS_GOT,
                infoList
        )
        return infoList.map { it.toModel() }
    }
}