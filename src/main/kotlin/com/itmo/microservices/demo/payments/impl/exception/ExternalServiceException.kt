package com.itmo.microservices.demo.payments.impl.exception

data class ExternalServiceException (
    override val message: String,
    val status: Int
) : Exception(message)