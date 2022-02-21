package com.itmo.microservices.demo.order.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*


@Service
class OrderServiceImpl(private val orderRepository: OrderRepository,
                       private val itemRepository: ItemRepository,
                       private val productsService: ProductsService,
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
               OrderServiceNotableEvents.ORDER_CREATED,
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

    override fun addItemToOrder(orderId: UUID, productId: UUID, amount: Int) {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        val product = productsService.getProduct(productId)
        val result = productsService.removeProduct(productId, amount)
        if (!result) {
            throw Exception("Can't remove $amount items from the warehouse")
        }
        println("Find by orderId: $productId")
        println(itemRepository.findAll())
        var orderItem = itemRepository.findByIdOrNull(productId)
        if (orderItem == null) {
            println("Null")
            orderItem = OrderItem(
                    id = productId,
                    title = product.title,
                    description = product.description,
                    price = product.price,
                    amount = amount
            )
            orderItem.id = productId
        } else {
            println("Not null")
            orderItem.amount = orderItem.amount?.plus(amount)
        }
        itemRepository.save(orderItem)
        println("Product was received")
        //order.itemsMap?.get(orderItem.id) ?: throw BadRequestException("Item $productId not found")
        order.itemsMap!![orderItem.id!!] = Amount(orderItem.amount)
        orderRepository.save(order)
        eventBus.post(ItemAddedToOrder(order.toModel()))
        eventLogger.info(
            OrderServiceNotableEvents.I_ITEM_ADDED_TO_ORDER,
            order
        )
    }

    override fun registerOrder(orderId: UUID): BookingDto {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw NotFoundException("Order $orderId not found")
        order.status = OrderStatus.BOOKED
        orderRepository.save(order)
        eventBus.post(OrderRegistered(order.toModel()))
        eventLogger.info(
            OrderServiceNotableEvents.I_ORDER_REGISTERED,
            order
        )
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
        return BookingDto(UUID.randomUUID(), setOf())
    }
}