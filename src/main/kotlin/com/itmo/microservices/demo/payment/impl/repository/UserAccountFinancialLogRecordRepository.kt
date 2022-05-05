package com.itmo.microservices.demo.payment.impl.repository

import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payment.impl.entity.UserAccountFinancialLogRecordEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserAccountFinancialLogRecordRepository: JpaRepository<UserAccountFinancialLogRecordEntity, Long> {
    fun getUserAccountFinancialLogRecordEntitiesByOrderId(orderId: UUID): List<UserAccountFinancialLogRecordDto>
}