package com.itmo.microservices.demo.delivery.api.messaging

import com.itmo.microservices.demo.delivery.api.model.DeliveryModel

data class DeliveryCreatedEvent(val task: DeliveryModel)

data class DeliveryDeletedEvent(val task: DeliveryModel)