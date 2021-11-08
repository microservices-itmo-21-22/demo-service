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
        } else
            throw NotFoundException("Please, choose another delivery time")
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

        slots.forEachIndexed { index, element ->
            slots[index] = element - deliverySearch(date, slotTimes[index], slotTimes[index + 1]) }

        var slotsAvailable = ""
        slots.forEach { slotsAvailable += it.toString() }

        return slotsVisualise(date, slots) //slotsAvailable
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

    fun  deliverySearch (date: String, time1: String, time2: String) =
        deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T" + time1),
            LocalDateTime.parse(date + "T" + time2)).size

    fun slotsVisualise(date: String, slots: MutableList<Int>): String {
        var visualise = "On $date available:\n"

        slots.forEachIndexed { index, element ->
            visualise += "slot#${index + 1}: $element of 5 (${slotTimes[index]} - ${slotTimes[index + 1]}),\n" }
        return visualise
    }

    fun getSlot(time: LocalDateTime): Boolean {
        val date = time.toString().substring(0, 10)
        var answer = false

        slotTimes.forEachIndexed { index, element ->
            if (time > LocalDateTime.parse(date + "T" + slotTimes[index]) &&
                time < LocalDateTime.parse(date + "T" + slotTimes[index + 1])) {
                answer = deliverySearch(date, slotTimes[index], slotTimes[index + 1]) < 5
            }
        }
        return answer
    }

    val slotTimes: MutableList<String> = mutableListOf("10:00:00",
                                                    "11:30:00",
                                                    "13:00:00",
                                                    "14:30:00",
                                                    "16:00:00",
                                                    "17:30:00"
                                                )
}