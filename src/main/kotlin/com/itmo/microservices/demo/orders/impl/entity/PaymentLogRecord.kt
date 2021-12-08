package com.itmo.microservices.demo.orders.impl.entity;

import javax.persistence.Entity;
import java.util.UUID;
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "paymentLogs")
class PaymentLogRecord {

    @Id
    var id : UUID = UUID.randomUUID()
    var timestamp: Long = 0
    var status: PaymentStatus = PaymentStatus.SUCCESS
    var amount: Long = 0
    var transactionId: UUID = UUID.randomUUID()

}
