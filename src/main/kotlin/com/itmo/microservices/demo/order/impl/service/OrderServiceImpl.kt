package com.itmo.microservices.demo.order.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.order.api.messaging.OrderCreatedEvent
import com.itmo.microservices.demo.order.api.messaging.OrderDeletedEvent
import com.itmo.microservices.demo.order.api.messaging.OrderGetAllEvent
import com.itmo.microservices.demo.order.api.messaging.OrderGetEvent
import com.itmo.microservices.demo.order.api.model.BookingDto
import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.entity.Amount
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.order.impl.entity.PaymentSubmission
import com.itmo.microservices.demo.order.impl.exception.BadRequestException
import com.itmo.microservices.demo.order.impl.logging.OrderServiceNotableEvents
import com.itmo.microservices.demo.order.impl.repository.ItemRepository
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import com.itmo.microservices.demo.order.impl.util.toModel
import com.itmo.microservices.demo.payments.api.model.PaymentSubmissionDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*


@Service
class OrderServiceImpl(private val orderRepository: OrderRepository,
                       private val itemRepository: ItemRepository,
                       private val eventBus: EventBus): OrderService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(OrderServiceImpl::class.java)
    }

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

   override fun createOrder(user: UserDetails): OrderDto {
       val order = OrderEntity(
               user.username,
               Date().time,
               OrderStatus.COLLECTING,
               mutableMapOf(),
               null,
               listOf()
       )
        orderRepository.save(order)
        log.info("Order ${order.id} was created")
       eventBus.post(OrderCreatedEvent(order.toModel()))
       eventLogger.info(
               OrderServiceNotableEvents.I_ORDER_CREATED,
               order
       )
       return order.toModel()
    }

    override fun getOrderById(orderId: UUID): OrderDto {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        eventBus.post(OrderGetEvent(order.toModel()))
        eventLogger.info(
                OrderServiceNotableEvents.I_ORDER_GOT,
                order
        )
        return order.toModel()
    }

    override fun addItemToOrder(orderId: UUID, itemId: UUID, amount: Int) {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        itemRepository.findByIdOrNull(itemId) ?: throw NotFoundException("Item $itemId not found")
        order.itemsMap?.get(itemId) ?: throw BadRequestException("Item $itemId not found")
        order.itemsMap!![itemId] = Amount(amount)
        orderRepository.save(order)
    }

    override fun registerOrder(orderId: UUID): BookingDto {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        order.status = OrderStatus.BOOKED
        orderRepository.save(order)
        return BookingDto(UUID.randomUUID(), setOf())
    }

    override fun setDeliveryTime(orderId: UUID, slotinSec: Int): BookingDto {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        order.deliveryDuration = slotinSec
        orderRepository.save(order)
        return BookingDto(UUID.randomUUID(), setOf())
    }

    override fun allOrders(): List<OrderDto> {
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