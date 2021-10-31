package com.itmo.microservices.demo.delivery.api.messaging

import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import java.util.*

data class DeliveryCreatedEvent(val user: DeliveryModel)

data class DeliveryDeletedEvent(val id: UUID)
