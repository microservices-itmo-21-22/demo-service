package com.itmo.microservices.demo.delivery.impl.service

import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.lib.common.delivery.dto.BookingDto
import io.prometheus.client.Counter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*


@Service
class DefaultDeliveryService : DeliveryService {
    @Value("\${service.name}")
    val applicationName : String = "";

    private val timeslotSetRequestCount: Counter =
        Counter.build()
            .name("timeslot_set_request_count")
            .help("Count of timeslot set requests")
            .labelNames("serviceName")
            .register();

    override fun getAvailableDeliverySlots(number: Int): List<LocalDateTime> {
        return listOf<LocalDateTime>()
    }

    override fun setDesiredDeliveryTime(order_id: UUID, slot_in_sec: Int): BookingDto {
        timeslotSetRequestCount.labels(applicationName).inc();

        return BookingDto(
            id = UUID.randomUUID(),
            failedItems = setOf(),
            orderId = order_id
        )
    }
}
