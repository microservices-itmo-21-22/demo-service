package com.itmo.microservices.demo.delivery.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.delivery.api.messaging.DeliveryCreatedEvent
import com.itmo.microservices.demo.delivery.api.messaging.DeliveryDeletedEvent
import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.delivery.impl.entity.Delivery
import com.itmo.microservices.demo.delivery.impl.logging.DeliveryServiceNotableEvents
import com.itmo.microservices.demo.delivery.impl.repository.DeliveryRepository
import com.itmo.microservices.demo.delivery.impl.util.toModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Suppress("UnstableApiUsage")
@Service
class DefaultDeliveryService(private val deliveryRepository: DeliveryRepository, private val eventBus: EventBus): DeliveryService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun getDeliveryInfo(deliveryId: UUID): DeliveryModel? = deliveryRepository.findByIdOrNull(deliveryId)?.toModel() ?:
    throw NotFoundException("Delivery with id : $deliveryId not found")

    override fun doDelivery(request: DeliveryModel) {
        val deliveryEntity = deliveryRepository.save(request.toEntity())
        eventBus.post(DeliveryCreatedEvent(deliveryEntity.toModel()))
        eventLogger.info(DeliveryServiceNotableEvents.I_DELIVERY_CREATED, deliveryEntity.id)
    }

    override fun deleteDelivery(deliveryId: UUID) {
        runCatching {
            deliveryRepository.deleteById(deliveryId)
        }.onSuccess {
            eventBus.post(DeliveryDeletedEvent(deliveryId))
            eventLogger.info(DeliveryServiceNotableEvents.I_DELIVERY_DELETED, deliveryId)
        }.onFailure {
            throw NotFoundException("Delivery with id = $deliveryId not found", it)
        }
    }

    fun DeliveryModel.toEntity(): Delivery =
        Delivery(id = this.id,
            type = this.type,
            warehouse = this.warehouse,
            deliveryDuration = this.deliveryDuration,
            address = this.address,
            courierCompany = this.courierCompany)

}