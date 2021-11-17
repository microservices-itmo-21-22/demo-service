package com.itmo.microservices.demo.orders.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.orders.api.messaging.OrderCreatedEvent
import com.itmo.microservices.demo.orders.api.messaging.OrderDeletedEvent
import com.itmo.microservices.demo.orders.api.messaging.PaymentAssignedEvent
import com.itmo.microservices.demo.orders.api.model.OrderModel
import com.itmo.microservices.demo.orders.api.model.PaymentModel
import com.itmo.microservices.demo.orders.api.service.OrderService
import com.itmo.microservices.demo.orders.impl.entity.Order
import com.itmo.microservices.demo.orders.impl.logging.OrderServiceNotableEvents
import com.itmo.microservices.demo.orders.impl.repository.OrderRepository
import com.itmo.microservices.demo.orders.impl.repository.PaymentRepository
import com.itmo.microservices.demo.orders.impl.util.toEntity
import com.itmo.microservices.demo.orders.impl.util.toModel
import com.itmo.microservices.demo.users.api.service.UserService
import org.hibernate.service.spi.InjectService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*
import javax.naming.OperationNotSupportedException

@Suppress("UnstableApiUsage")
@Service
class DefaultOrderService(private val orderRepository: OrderRepository,
                          private val paymentRepository: PaymentRepository,
                          private val eventBus: EventBus,
                          private val userService: UserService) : OrderService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun getOrdersByUsername(userName: String): List<OrderModel> {
        val userId = getUserIdByName(userName)
        val orders = orderRepository.findAll()
        val result = mutableListOf<OrderModel>()
        for (order in orders) {
            if(order.userId == userId) {
                result.add(order.toModel())
            }
        }
        return result
    }

    override fun getOrder(orderId: UUID): OrderModel {
        return orderRepository.findByIdOrNull(orderId)?.toModel() ?: throw NotFoundException("Order $orderId not found")
    }

    override fun createOrderFromBusket(busketId: UUID, username : String): OrderModel {
        val userId = getUserIdByName(username)
        val order = Order(UUID.randomUUID(), 0, busketId, userId, Date());
        orderRepository.save(order)
        val orderModel = order.toModel()
        eventBus.post(OrderCreatedEvent(orderModel))
        eventLogger.info(OrderServiceNotableEvents.I_ORDER_CREATED, order)
        return orderModel
    }

    override fun deleteOrder(orderId: UUID, userName : String) {
        val userId = getUserIdByName(userName);
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        if(order.userId != userId)
            throw AccessDeniedException("Cannot delete order that was not created by you")
        eventBus.post(OrderDeletedEvent(order.toModel()))
        eventLogger.info(OrderServiceNotableEvents.I_ORDER_DELETED, order)
        order.status = 4
        orderRepository.save(order)
    }

    override fun assignPayment(orderId: UUID, payment : PaymentModel) {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        if(order.status != 0)
            throw OperationNotSupportedException("Order has already been paid")
        order.status = 1
        orderRepository.save(order)
        val paymentEntity = payment.toEntity()
        eventBus.post(PaymentAssignedEvent(payment))
        eventLogger.info(OrderServiceNotableEvents.I_PAYMENT_ASSIGNED, paymentEntity)
        paymentRepository.save(paymentEntity)
    }

    fun getUserIdByName(userName: String): UUID {
        // TODO
        return UUID.fromString("00000000-0000-0000-0000-000000000000")
    }
}