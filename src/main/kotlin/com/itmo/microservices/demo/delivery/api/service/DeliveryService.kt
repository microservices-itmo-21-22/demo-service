package com.itmo.microservices.demo.delivery.api.service

import com.itmo.microservices.demo.lib.common.delivery.dto.BookingDto
import java.time.LocalDateTime
import java.util.*

interface DeliveryService {
    fun getAvailableDeliverySlots(number: Int): List<LocalDateTime>
    fun setDesiredDeliveryTime(order_id: UUID, slot_in_sec: Int): BookingDto
}
