package com.itmo.microservices.demo.delivery.api.model

import jdk.management.jfr.SettingDescriptorInfo
import java.util.*

class BookingDto {
    var id: UUID = UUID.randomUUID()
    var failedItems: Set<UUID> = HashSet<UUID>()
}