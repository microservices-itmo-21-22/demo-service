package com.itmo.microservices.demo.order.impl.util

import com.itmo.microservices.demo.order.api.model.PaymentLogRecordDto
import com.itmo.microservices.demo.order.impl.entity.PaymentLogRecord

fun PaymentLogRecord.toModel(): PaymentLogRecordDto = PaymentLogRecordDto(
        transactionId = this.transactionId,
        timestamp = this.timestamp,
        status = this.status,
        amount = this.amount
)

fun PaymentLogRecordDto.toEntity(): PaymentLogRecord = PaymentLogRecord(
    this.timestamp,
    this.status,
    this.amount
)