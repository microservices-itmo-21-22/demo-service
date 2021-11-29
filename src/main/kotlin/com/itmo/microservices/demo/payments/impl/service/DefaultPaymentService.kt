package com.itmo.microservices.demo.payments.impl.service

import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.payments.api.model.FinancialOperationType
import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payments.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payments.api.service.PaymentService
import com.itmo.microservices.demo.payments.impl.entity.UserAccountFinancialLogRecordEntity
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import com.itmo.microservices.demo.payments.impl.repository.PaymentRepository
import com.itmo.microservices.demo.payments.impl.repository.UserAccountFinancialLogRecordRepository
import com.itmo.microservices.demo.payments.impl.util.toModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Suppress("UnstableApiUsage")
@Service
class DefaultPaymentService(private val paymentRepository: PaymentRepository,
                            private val orderRepository: OrderRepository,
                            private val userAccountFinancialLogRecordRepository: UserAccountFinancialLogRecordRepository) : PaymentService {

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
        orderId?.let {
            val infoList = userAccountFinancialLogRecordRepository.findAllByOrderId(orderId)
            return infoList.map { it.toModel() }
        }
        val orders = orderRepository.findAllByUsername(author.username)
        val infoList = orders.mapNotNull { it.id }.flatMap { userAccountFinancialLogRecordRepository.findAllByOrderId(it) }
        return infoList.map { it.toModel() }
    }
}