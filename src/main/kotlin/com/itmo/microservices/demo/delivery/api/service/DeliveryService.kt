package com.itmo.microservices.demo.delivery.api.service

import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import java.util.*

interface DeliveryService {
    fun getDelivery(deliveryId : UUID) : DeliveryModel
    fun addDelivery(delivery: DeliveryModel)
    fun deleteDelivery(deliveryId: UUID)
    fun finalizeDelivery(deliveryId : UUID)
}