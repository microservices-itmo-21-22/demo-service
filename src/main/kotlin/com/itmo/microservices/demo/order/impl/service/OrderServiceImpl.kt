package com.itmo.microservices.demo.order.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.order.api.messaging.OrderCreatedEvent
import com.itmo.microservices.demo.order.api.messaging.OrderDeletedEvent
import com.itmo.microservices.demo.order.api.messaging.OrderGetAllEvent
import com.itmo.microservices.demo.order.api.messaging.OrderGetEvent
import com.itmo.microservices.demo.order.api.model.OrderModel
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.order.impl.logging.OrderServiceNotableEvents
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import com.itmo.microservices.demo.order.impl.util.toModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*


@Service
class OrderServiceImpl(private val orderRepository: OrderRepository,
                       private val eventBus: EventBus): OrderService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(OrderServiceImpl::class.java)
    }

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

   override fun createOrder(orderModel: OrderModel, author: UserDetails): OrderModel {
        var order = OrderEntity(
                date = orderModel.date
        )
        order = orderRepository.save(order)
        log.info("Order ${order.id} was created")
       eventBus.post(OrderCreatedEvent(orderModel))
       eventLogger.info(
               OrderServiceNotableEvents.I_ORDER_CREATED,
               order
       )
       return order.toModel()
    }

    override fun getOrderById(orderId: UUID): OrderModel {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        eventBus.post(OrderGetEvent(order.toModel()))
        eventLogger.info(
                OrderServiceNotableEvents.I_ORDER_GOT,
                order
        )
        return order.toModel()
    }

    override fun allOrders(): List<OrderModel> {
        eventBus.post(OrderGetAllEvent(null))
        eventLogger.info(
                OrderServiceNotableEvents.I_ORDER_GOT_ALL,
                null
        )
        return orderRepository.findAll().map { it.toModel() }
    }

    override fun deleteOrderById(orderId: UUID) {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Busket $orderId not found")
        orderRepository.delete(order)
        eventBus.post(OrderDeletedEvent(order.toModel()))
        eventLogger.info(
                OrderServiceNotableEvents.I_ORDER_DELETED,
                order
        )
    }

}