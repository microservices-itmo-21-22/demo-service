package com.itmo.microservices.demo.order.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.common.metrics.DemoServiceMetricsCollector
import com.itmo.microservices.demo.order.api.messaging.*
import com.itmo.microservices.demo.order.api.model.BookingDto
import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.entity.Amount
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.order.impl.entity.OrderItem
import com.itmo.microservices.demo.order.impl.logging.OrderServiceNotableEvents
import com.itmo.microservices.demo.order.impl.repository.ItemRepository
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import com.itmo.microservices.demo.order.impl.util.toModel
import com.itmo.microservices.demo.products.api.service.ProductsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit


@Service
@EnableScheduling
class OrderServiceImpl(private val orderRepository: OrderRepository,
                       private val itemRepository: ItemRepository,
                       private val productsService: ProductsService,
                       private val eventBus: EventBus): OrderService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    @Autowired
    private lateinit var metricsCollector: DemoServiceMetricsCollector

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

       eventBus.post(OrderCreatedEvent(order.toModel()))
       eventLogger.info(
           OrderServiceNotableEvents.ORDER_CREATED,
           order
       )

       metricsCollector.orderCreatedCounter.increment()
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

    override fun addItemToOrder(orderId: UUID, productId: UUID, amount: Int) {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        val product = productsService.getProduct(productId)
        val result = productsService.removeProduct(productId, amount)
        if (!result) {
            metricsCollector.itemBookRequestFailedCounter.increment()
            throw Exception("Can't remove $amount items from the warehouse")
        }
        var orderItem = itemRepository.findByIdOrNull(productId)
        if (orderItem == null) {
            orderItem = OrderItem(
                    id = productId,
                    title = product.title,
                    description = product.description,
                    price = product.price,
                    amount = amount
            )
            orderItem.id = productId
        } else {
            orderItem.amount = orderItem.amount?.plus(amount)
        }
        itemRepository.save(orderItem)
        order.itemsMap!![orderItem.id!!] = Amount(orderItem.amount)
        order.timeCreated = Date().time
        changeOrderStatus(order, OrderStatus.COLLECTING)

        eventBus.post(ItemAddedToOrder(order.toModel()))
        eventLogger.info(
            OrderServiceNotableEvents.I_ITEM_ADDED_TO_ORDER,
            order
        )

        metricsCollector.itemBookRequestSuccessCounter.increment()
        metricsCollector.itemAddedCounter.increment()
        if (order.status == OrderStatus.BOOKED) {
            metricsCollector.addToFinilizedOrderRequestCounter.increment()
        }
    }

    override fun registerOrder(orderId: UUID): BookingDto {
        val order = orderRepository.findByIdOrNull(orderId)
        if (order == null) {
            metricsCollector.finalizationAttemptFailedCounter.increment()
            throw NotFoundException("Order $orderId not found")
        }
        changeOrderStatus(order, OrderStatus.BOOKED)

        eventBus.post(OrderRegistered(order.toModel()))
        eventLogger.info(
            OrderServiceNotableEvents.I_ORDER_REGISTERED,
            order
        )

        metricsCollector.finalizationAttemptSuccessCounter.increment()
        metricsCollector.finalizationDurationSummary.record(Date().time - order.timeCreated!!, TimeUnit.MILLISECONDS)
        return BookingDto(UUID.randomUUID(), setOf())
    }

    override fun setDeliveryTime(orderId: UUID, slotinSec: Int): BookingDto {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        order.deliveryDuration = slotinSec
        orderRepository.save(order)

        eventBus.post(OrderDated(order.toModel()))
        eventLogger.info(
            OrderServiceNotableEvents.I_ORDER_DATED,
            order
        )

        metricsCollector.timeslotSetCounter.increment()
        return BookingDto(UUID.randomUUID(), setOf())
    }


    @Scheduled(fixedRate = 60000)
    override fun getOrdersInStatus() {
        val count = orderRepository
                .findAll()
                .filter {
                    it.status == OrderStatus.COLLECTING
                }
                .count()
        metricsCollector.ordersInStatusHistogram.observe(count.toDouble())
    }

    private fun changeOrderStatus(order: OrderEntity, status: OrderStatus) {
        val prevStatus = order.status
        order.status = status
        orderRepository.save(order)
        when {
            prevStatus == OrderStatus.COLLECTING && status == OrderStatus.DISCARD ->
                metricsCollector.fromCollectingToDiscardStatusCounter.increment()
            prevStatus == OrderStatus.DISCARD && status == OrderStatus.COLLECTING ->
                metricsCollector.fromDiscardToCollectingStatusCounter.increment()
            prevStatus == OrderStatus.COLLECTING && status == OrderStatus.BOOKED ->
                metricsCollector.fromCollectingToBookedStatusCounter.increment()
            prevStatus == OrderStatus.BOOKED && status == OrderStatus.PAID ->
                metricsCollector.fromBookedToPaidStatusCounter.increment()
        }
    }

    private val minutesForRefund = 15
    private val minutesForDelete = 60

    @Scheduled(fixedRate = 60000)
    fun checkForDiscard() {
        val currentTime = Date().time
        val orders = orderRepository.findAll()
            .filter { it.status == OrderStatus.COLLECTING }
            .filter { it.timeCreated != null }

        for (order in orders) {
            val orderCreated = order.timeCreated!!
            val diff = currentTime - orderCreated
            val minutes = (diff / 60000).toInt()
            if (minutes >= minutesForRefund) {
                changeOrderStatus(order, OrderStatus.DISCARD)
            }
        }

        val refundCount = orderRepository.findAll().count { it.status == OrderStatus.DISCARD }
        metricsCollector.currentAbandonedOrderNumGauge.set(refundCount)
    }

    @Scheduled(fixedRate = 60000)
    fun checkForDelete() {
        val currentTime = Date().time
        val orders = orderRepository.findAll()
            .filter { it.status == OrderStatus.DISCARD }
            .filter { it.timeCreated != null }

        for (order in orders) {
            val orderCreated = order.timeCreated!!
            val diff = currentTime - orderCreated
            val minutes = (diff / 60000).toInt()
            if (minutes >= minutesForDelete) {
                orderRepository.delete(order)
                metricsCollector.discardedOrdersCounter.increment()
            }
        }

    }
}