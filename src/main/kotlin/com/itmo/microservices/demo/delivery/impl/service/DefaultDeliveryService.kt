package com.itmo.microservices.demo.delivery.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.delivery.api.messaging.DeliveryCreatedEvent
import com.itmo.microservices.demo.delivery.api.messaging.DeliveryDeletedEvent
import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.delivery.impl.logging.DeliveryServiceNotableEvents
import com.itmo.microservices.demo.delivery.impl.repository.DeliveryRepository
import com.itmo.microservices.demo.delivery.impl.util.toEntity
import com.itmo.microservices.demo.delivery.impl.util.toModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultDeliveryService(private val deliveryRepository: DeliveryRepository,
                                private val eventBus: EventBus) : DeliveryService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun getDelivery(deliveryId: UUID): DeliveryModel {
        return deliveryRepository.findByIdOrNull(deliveryId)?.toModel() ?: throw NotFoundException("Order $deliveryId not found")
    }

    override fun addDelivery(delivery: DeliveryModel) {
        deliveryRepository.save(delivery.toEntity())
        eventBus.post(DeliveryCreatedEvent(delivery))
        eventLogger.info(DeliveryServiceNotableEvents.I_DELIVERY_CREATED, delivery.toEntity())
    }

    override fun deleteDelivery(deliveryId: UUID) {
        val delivery = deliveryRepository.findByIdOrNull(deliveryId) ?: throw NotFoundException("Order $deliveryId not found")
        eventBus.post(DeliveryDeletedEvent(delivery.toModel()))
        eventLogger.info(DeliveryServiceNotableEvents.I_DELIVERY_DELETED, delivery)
        deliveryRepository.deleteById(deliveryId)
    }

    override fun finalizeDelivery(deliveryId: UUID) {

        TODO("Not yet implemented")
    }
}