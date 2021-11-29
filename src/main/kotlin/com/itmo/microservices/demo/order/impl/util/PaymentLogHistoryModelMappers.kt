package com.itmo.microservices.demo.order.impl.util

import com.itmo.microservices.demo.order.api.model.PaymentLogRecordDto
import com.itmo.microservices.demo.order.impl.entity.PaymentLogRecordEntity

fun PaymentLogRecordEntity.toModel(): PaymentLogRecordDto = PaymentLogRecordDto(
        transactionId = this.transactionId,
        timestamp = this.timestamp,
        status = this.status,
        amount = this.amount
)

fun PaymentLogRecordDto.toEntity(): PaymentLogRecordEntity = PaymentLogRecordEntity(
    this.timestamp,
    this.status,
    this.amount
)