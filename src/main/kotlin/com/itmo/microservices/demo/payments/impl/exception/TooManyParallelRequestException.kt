package com.itmo.microservices.demo.payments.impl.exception

data class TooManyParallelRequestException (
        override val message: String?
) : Exception(message)