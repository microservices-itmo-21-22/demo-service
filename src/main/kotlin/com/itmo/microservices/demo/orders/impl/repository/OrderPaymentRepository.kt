package com.itmo.microservices.demo.orders.impl.repository

import com.itmo.microservices.demo.orders.impl.entity.OldPayment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderPaymentRepository : JpaRepository<OldPayment, UUID> {}