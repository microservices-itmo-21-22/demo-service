package com.itmo.microservices.demo.delivery.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DeliveryInfoRecord(
        val outcome: DeliverySubmissionOutcome? = null,
        val preparedTime: Long? = null,
        val attempts: Int = 1,
        val submittedTime: Long? = null,
        val transactionId: UUID? = null,
        val submissionStartedTime: Long? = null
)