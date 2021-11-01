package com.itmo.microservices.demo.order.impl.dao

import com.itmo.microservices.demo.order.impl.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderRepository : JpaRepository<Order?, UUID?>