package com.itmo.microservices.demo.payments.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.common.metrics.DemoServiceMetricsCollector
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import com.itmo.microservices.demo.order.impl.service.OrderServiceImpl
import com.itmo.microservices.demo.payments.api.model.FinancialOperationType
import com.itmo.microservices.demo.payments.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payments.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payments.api.service.PaymentService
import com.itmo.microservices.demo.payments.impl.entity.Transaction
import com.itmo.microservices.demo.payments.impl.entity.UserAccountFinancialLogRecordEntity
import com.itmo.microservices.demo.payments.impl.logging.PaymentServiceNotableEvents
import com.itmo.microservices.demo.payments.impl.repository.PaymentRepository
import com.itmo.microservices.demo.payments.impl.repository.TransactionRepository
import com.itmo.microservices.demo.payments.impl.repository.UserAccountFinancialLogRecordRepository
import com.itmo.microservices.demo.payments.impl.util.toModel
import com.itmo.microservices.demo.products.impl.repository.ProductsRepository
import com.itmo.microservices.demo.products.impl.service.DefaultProductsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


@Suppress("UnstableApiUsage")
@Service
class DefaultPaymentService(
    private val paymentRepository: PaymentRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionRequestService: TransactionRequestService,
    private val orderRepository: OrderRepository,
    private val orderService: OrderService,
    private val userAccountFinancialLogRecordRepository: UserAccountFinancialLogRecordRepository,
    private val eventBus: EventBus
) : PaymentService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(OrderServiceImpl::class.java)
    }

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    @Autowired
    private lateinit var metricsCollector: DemoServiceMetricsCollector

    override fun pay(orderId: UUID): PaymentSubmissionDto {
        println(
            "Pay method. "
                    + Thread.currentThread().getName()
        );
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        order.status = OrderStatus.PAID
        metricsCollector.averagedBookingToPayTime.record(System.nanoTime() - order.timeUpdated!!, TimeUnit.NANOSECONDS)
        orderRepository.save(order)
        val amount = order.itemsMap?.entries?.sumOf {
            it.value.amount?.times(orderService.getOrderItemById(it.key).price!!) ?: 0
        }
        metricsCollector.revenueCounter.increment(amount!!.toDouble())
        metricsCollector.externalSystemExpensePaymentCounter.increment(amount.toDouble())
        val record = UserAccountFinancialLogRecordEntity(
            UUID.randomUUID(),
            FinancialOperationType.WITHDRAW,
            amount,
            order.id,
            Date().time
        )
        userAccountFinancialLogRecordRepository.save(record)

        val transactionResponse = transactionRequestService.postRequest()
        val paymentSubmission = PaymentSubmissionDto(transactionResponse?.id, Date().time)
        eventBus.post(paymentSubmission)
        eventLogger.info(
            PaymentServiceNotableEvents.I_ORDER_PAID,
            paymentSubmission
        )
        CompletableFuture.supplyAsync(Supplier {
            transactionResponse.let { transactionRequestService.poll(it?.id ?: UUID.randomUUID()) }
        })

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
        val infoList =
            orders.mapNotNull { it.id }.flatMap { userAccountFinancialLogRecordRepository.findAllByOrderId(it) }
        eventLogger.info(
            PaymentServiceNotableEvents.I_PAYMENTS_GOT,
            infoList
        )
        return infoList.map { it.toModel() }
    }

    override fun getTransactions(): List<Transaction> =
        transactionRepository.findAll()

}