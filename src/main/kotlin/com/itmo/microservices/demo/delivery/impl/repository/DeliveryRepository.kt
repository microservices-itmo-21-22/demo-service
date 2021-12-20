package com.itmo.microservices.demo.delivery.impl.repository


import com.itmo.microservices.demo.delivery.impl.entity.Delivery
import com.itmo.microservices.demo.delivery.impl.entity.DeliverySubmissionOutcome
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DeliveryRepository:JpaRepository<Delivery, String>{
}
