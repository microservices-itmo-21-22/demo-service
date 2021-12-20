package com.itmo.microservices.demo.delivery.impl.utils

import com.itmo.microservices.demo.delivery.api.model.DeliveryInfoRecordModel
import com.itmo.microservices.demo.delivery.impl.entity.DeliveryInfoRecord

fun DeliveryInfoRecord.toModel():DeliveryInfoRecordModel = kotlin.runCatching {
    DeliveryInfoRecordModel(
        outcome=this.outcome!!,
        preparedTime=this.preparedTime!!,
        attempts=this.attempts!!,
        submittedTime=this.submittedTime!!,
        transactionId=this.transactionId!!,
        submissionStartedTime=this.submissionStartedTime!!
    )
}.getOrElse {exception -> throw IllegalStateException("Some of delivery info record fields are null", exception)  }