package com.itmo.microservices.demo.payments.impl.util

import com.itmo.microservices.demo.payments.api.model.TransactionDto
import com.itmo.microservices.demo.payments.impl.entity.Transaction

fun TransactionDto.toEntity(): Transaction = Transaction(
        id = this.id,
        status = this.status,
        submitTime = this.submitTime,
        completedTime = this.completedTime,
        cost = this.cost,
        delta = this.delta
)

fun Transaction.toModel(): TransactionDto = TransactionDto(
        id = this.id,
        status = this.status,
        submitTime = this.submitTime,
        completedTime = this.completedTime,
        cost = this.cost,
        delta = this.delta
)