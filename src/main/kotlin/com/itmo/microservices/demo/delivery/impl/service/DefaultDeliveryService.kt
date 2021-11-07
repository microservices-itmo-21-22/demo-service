package com.itmo.microservices.demo.delivery.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.delivery.api.messaging.DeliveryCreatedEvent
import com.itmo.microservices.demo.delivery.api.messaging.DeliveryDeletedEvent
import com.itmo.microservices.demo.delivery.api.model.DeliveryDTO
import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.delivery.impl.entity.Delivery
import com.itmo.microservices.demo.delivery.impl.logging.DeliveryServiceNotableEvents
import com.itmo.microservices.demo.delivery.impl.repository.DeliveryRepository
import com.itmo.microservices.demo.delivery.impl.util.toModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Suppress("UnstableApiUsage")
@Service
class DefaultDeliveryService(private val deliveryRepository: DeliveryRepository,
                             private val eventBus: EventBus): DeliveryService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun doDelivery(request: DeliveryDTO, user: UserDetails) {
        if (getSlot(request.preferredDeliveryTime)) {
            val deliveryEntity = request.toEntity(user)
            deliveryRepository.save(deliveryEntity)
            eventBus.post(DeliveryCreatedEvent(deliveryEntity.toModel()))
            eventLogger.info(DeliveryServiceNotableEvents.I_DELIVERY_CREATED, deliveryEntity.id)
        } else throw AccessDeniedException("Please, choose another delivery time")
    }

    override fun getDeliveryInfo(deliveryId: UUID, user: UserDetails): DeliveryModel {
        val delivery = deliveryRepository.findByIdOrNull(deliveryId)?.toModel()?:
            throw NotFoundException("Delivery with id : $deliveryId not found")
        if (delivery.user != user.username)
            throw AccessDeniedException("Cannot get delivery that was not created by you")
        return delivery
    }

    override fun getDeliverySlots(date: String): String {
        val slots: MutableList<Int> = mutableListOf(5, 5, 5, 5, 5)
        slots[0] = slots[0] -
                deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T10:00:00"),
            LocalDateTime.parse(date + "T11:30:00")).size
        slots[1] = slots[1] -
                deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T11:30:00"),
            LocalDateTime.parse(date + "T13:00:00")).size
        slots[2] = slots[2] -
                deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T13:00:00"),
            LocalDateTime.parse(date + "T14:30:00")).size
        slots[3] = slots[3] -
                deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T14:30:00"),
            LocalDateTime.parse(date + "T16:00:00")).size
        slots[4] = slots[4] -
                deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T16:00:00"),
            LocalDateTime.parse(date + "T17:30:00")).size
        return slotsVisualise(date, slots)
    }

    override fun allDeliveries(user: UserDetails) = deliveryRepository.findAllByUser(user.username)
        .map { it.toModel() }

    override fun deleteDelivery(deliveryId: UUID, user: UserDetails) {
        val delivery = deliveryRepository.findByIdOrNull(deliveryId)?.toModel()?:
        throw NotFoundException("Delivery with id : $deliveryId not found")
        if (delivery.user != user.username)
            throw AccessDeniedException("Cannot delete delivery that was not created by you")
        runCatching {
            deliveryRepository.deleteById(deliveryId)
        }.onSuccess {
            eventBus.post(DeliveryDeletedEvent(deliveryId))
            eventLogger.info(DeliveryServiceNotableEvents.I_DELIVERY_DELETED, deliveryId)
        }.onFailure {
            throw NotFoundException("Delivery with id = $deliveryId not found", it)
        }
    }

    fun DeliveryDTO.toEntity(user: UserDetails): Delivery =
        Delivery(id = UUID.randomUUID(),
            user = user.username,
            type = this.type,
            warehouse = this.warehouse,
            preferredDeliveryTime = this.preferredDeliveryTime,
            address = this.address,
            courierCompany = "CDEC"
        )

    fun slotsVisualise(date: String, slots: MutableList<Int>): String =
    "On $date available:\n" +
            "slot#1: ${slots[0]} of 5 (10.00-11.30),\n" +
            "slot#2: ${slots[1]} of 5 (11.30-13.00),\n" +
            "slot#3: ${slots[2]} of 5 (13.00-14.30),\n" +
            "slot#4: ${slots[3]} of 5 (14.30-16.00),\n" +
            "slot#5: ${slots[4]} of 5 (16.00-17.30)"

    fun getSlot(time: LocalDateTime): Boolean {
        val date = time.toString().substring(0, 10)

        if (time > LocalDateTime.parse(date + "T10:00:00") &&
            time < LocalDateTime.parse(date + "T11:30:00")) {
            return deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T10:00:00"),
                LocalDateTime.parse(date + "T11:30:00")).size < 5
        } else if (time > LocalDateTime.parse(date + "T11:30:00") &&
            time < LocalDateTime.parse(date + "T13:00:00")) {
            return deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T11:30:00"),
                LocalDateTime.parse(date + "T13:00:00")).size < 5
        } else if (time > LocalDateTime.parse(date + "T13:00:00") &&
            time < LocalDateTime.parse(date + "T14:30:00")) {
            return deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T13:00:00"),
                LocalDateTime.parse(date + "T14:30:00")).size < 5
        } else if (time > LocalDateTime.parse(date + "T14:30:00") &&
            time < LocalDateTime.parse(date + "T16:00:00")) {
            return deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T14:30:00"),
                LocalDateTime.parse(date + "T16:00:00")).size < 5
        } else if (time > LocalDateTime.parse(date + "T16:00:00") &&
            time < LocalDateTime.parse(date + "T17:30:00")) {
            return deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T16:00:00"),
                LocalDateTime.parse(date + "T17:30:00")).size < 5
        } else return false
    }
}