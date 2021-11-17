package com.itmo.microservices.demo.delivery.impl.service

import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.lib.common.delivery.dto.BookingDto
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class DefaultDeliveryService : DeliveryService {
    override fun getAvailableDeliverySlots(number: Int): List<LocalDateTime> {
        return listOf<LocalDateTime>()
    }

    override fun setDesiredDeliveryTime(order_id: UUID, slot_in_sec: Int): BookingDto {
        return BookingDto(
            id = UUID.randomUUID(),
            failedItems = setOf(),
            orderId = order_id
        )
    }
}
