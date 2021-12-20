package com.itmo.microservices.demo.delivery.api.model

import com.itmo.microservices.demo.delivery.impl.entity.DeliverySubmissionOutcome
import java.util.*

data class DeliveryInfoRecordModel(
    var outcome: DeliverySubmissionOutcome,
    var preparedTime: Long,
    var attempts: Int,
    var submittedTime: Long,
    var transactionId: UUID,
    var submissionStartedTime:Long
)