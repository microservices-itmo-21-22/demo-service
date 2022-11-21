package com.itmo.microservices.demo.delivery.api.service

import com.itmo.microservices.demo.delivery.api.model.BookingDTO
import com.itmo.microservices.demo.delivery.api.model.DeliveryInfoRecord
import java.util.*

interface DeliveryService {
    fun getDeliverySlots(number: Int): List<Int>
    fun setTime(orderId: UUID, slotInSec: Int): BookingDTO
    fun getDeliveryHistory(orderId: Any): List<DeliveryInfoRecord>
}