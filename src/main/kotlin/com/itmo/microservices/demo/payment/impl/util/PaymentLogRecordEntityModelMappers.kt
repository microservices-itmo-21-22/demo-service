package com.itmo.microservices.demo.payment.impl.util

import com.itmo.microservices.demo.payment.api.model.PaymentLogRecordDto
import com.itmo.microservices.demo.payment.impl.entity.PaymentLogRecordEntity

fun PaymentLogRecordDto.toEntity(): PaymentLogRecordEntity = PaymentLogRecordEntity(
        this.timestamp,
        this.status,
        this.amount,
        this.transactionId
)

fun PaymentLogRecordEntity.toModel(): PaymentLogRecordDto = PaymentLogRecordDto(
        this.transactionId,
        this.timestamp,
        this.status,
        this.amount
)