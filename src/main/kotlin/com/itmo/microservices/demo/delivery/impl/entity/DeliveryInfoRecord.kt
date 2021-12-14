package com.itmo.microservices.demo.delivery.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

enum class DeliverySubmissionOutcome { SUCCESS, FAILURE, EXPIRED }

@Entity
class DeliveryInfoRecord {
    @Id
    var id: UUID? = UUID.randomUUID()
    var outcome: DeliverySubmissionOutcome? = null
    var preparedTime: Long? = null
    var attempts: Int? = null
    var submittedTime: Long? = null
    var transactionId: UUID? = null
    var submissionStartedTime: Long? = null

    constructor()

    constructor(
        outcome: DeliverySubmissionOutcome, preparedTime: Long, attempts: Int,
        submittedTime: Long,
        transactionId: UUID,
        submissionStartedTime: Long
    ) {
        this.outcome = outcome
        this.preparedTime = preparedTime
        this.attempts = attempts
        this.submittedTime = submittedTime
        this.transactionId = transactionId
        this.submissionStartedTime = submissionStartedTime
    }
}