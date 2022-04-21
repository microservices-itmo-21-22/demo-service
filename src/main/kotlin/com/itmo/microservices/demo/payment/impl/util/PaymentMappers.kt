package com.itmo.microservices.demo.payment.impl.util

import com.itmo.microservices.demo.payment.api.model.PaymentLogRecord
import com.itmo.microservices.demo.payment.impl.entity.PaymentLogRecordEntity

fun PaymentLogRecordEntity.toModel():
        PaymentLogRecord = PaymentLogRecord(timestamp, status, amount, transactionId)
