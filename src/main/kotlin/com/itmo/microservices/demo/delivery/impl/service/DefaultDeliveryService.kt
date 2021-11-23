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
import kong.unirest.Unirest
import kong.unirest.json.JSONObject
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.*


@Service
class DefaultDeliveryService(private val deliveryRepository: DeliveryRepository,
                             private val eventBus: EventBus) : DeliveryService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun getDelivery(deliveryId: UUID): DeliveryModel {
        return deliveryRepository.findByIdOrNull(deliveryId)?.toModel() ?: throw NotFoundException("Order $deliveryId not found")
    }

    override fun getDeliveryByOrder(orderId: UUID): List<DeliveryModel> {
        return deliveryRepository.findByOrderId(orderId).map { it.toModel() }
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
        val delivery = deliveryRepository.findByIdOrNull(deliveryId) ?: throw NotFoundException("Order $deliveryId not found")
        //there is nothing, yet
        eventBus.post(DeliveryDeletedEvent(delivery.toModel()))
        eventLogger.info(DeliveryServiceNotableEvents.I_DELIVERY_DELIVERED, delivery)
    }

    override fun getDeliverySlots(number: Int): List<Int> {
        if (number <= 0) {
            throw IllegalArgumentException("Number of slots supposed to be more than 0, not $number")
        }
        //access API, this transaction imitates receiving information about available slots
        val json = transaction()
        //calculate all available slots, choose number of first
        val temp : List<Int> = listOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20).minus(deliveryRepository.getAllSlots()
            .toSet())// some magic
        return temp.take(number)
    }

    override fun getDeliveryBySlot(slotInSec: Int) : DeliveryModel{
        val delivery = deliveryRepository.findBySlot(slotInSec)?.toModel() ?: throw NotFoundException("Slot $slotInSec not found")
        return delivery
    }

    override fun reserveDeliverySlots(deliveryId: UUID, slotInSec: Int) {
        //access API, this transaction imitates sending reservation
        transaction()
        //check if available and reserve
        val delivery = deliveryRepository.findByIdOrNull(deliveryId)
        if (deliveryRepository.findBySlot(slotInSec) == null && deliveryRepository.findByIdOrNull(deliveryId) != null){
            delivery!!.slot = slotInSec
            deliveryRepository.save(delivery)
        } else {
            throw IllegalArgumentException("Didn't found by Id or Slot already taken")
        }

    }

    fun transaction() : JSONObject{
        var tries = 0
        while(true){
            tries++
            if (tries == 6) throw RuntimeException("Failed to send request")

            var response = Unirest.post("http://77.234.215.138:30027/transactions/")
                .header("Content-Type", "application/json;IEEE754Compatible=true")
                .body("{\"clientSecret\": \"7d65037f-e9af-433e-8e3f-a3da77e019b1\"}")
                .asJson()
            if (response.status != 200){
                continue
            }
            var json = response.body.`object`
            if (json.get("status").equals("SUCCESS")){
                return json
            }
        }
    }
}