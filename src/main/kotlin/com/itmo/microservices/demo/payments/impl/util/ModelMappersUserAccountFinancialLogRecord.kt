package com.itmo.microservices.demo.payments.impl.util

import com.itmo.microservices.demo.payments.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payments.impl.entity.UserAccountFinancialLogRecordEntity


fun UserAccountFinancialLogRecordDto.toEntity(): UserAccountFinancialLogRecordEntity = UserAccountFinancialLogRecordEntity(
        paymentTransactionId = this.paymentTransactionId,
        type = this.type,
        amount = this.amount,
        orderId = this.orderId,
        timestamp = this.timestamp
)

fun UserAccountFinancialLogRecordEntity.toModel(): UserAccountFinancialLogRecordDto = UserAccountFinancialLogRecordDto(
        paymentTransactionId = this.paymentTransactionId,
        type = this.type,
        amount = this.amount,
        orderId = this.orderId,
        timestamp = this.timestamp
)