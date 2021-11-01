package com.itmo.microservices.demo.delivery.api.service

import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface DeliveryService {
    fun getDeliveryInfo(deliveryId: UUID): DeliveryModel?
    fun doDelivery(request: DeliveryModel, user: UserDetails)
    fun allDeliveries(user: UserDetails): List<DeliveryModel>
    fun deleteDelivery(deliveryId: UUID)
}