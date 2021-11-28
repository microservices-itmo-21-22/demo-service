package com.itmo.microservices.demo.payments.impl.repository

import com.itmo.microservices.demo.payments.impl.entity.UserAccountFinancialLogRecordEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserAccountFinancialLogRecordRepository : JpaRepository<UserAccountFinancialLogRecordEntity, UUID> {
    fun findAllByOrderId(orderId: UUID): List<UserAccountFinancialLogRecordEntity>
}