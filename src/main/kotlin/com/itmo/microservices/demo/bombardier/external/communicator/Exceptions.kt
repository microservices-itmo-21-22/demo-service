package com.itmo.microservices.demo.bombardier.external.communicator

import okhttp3.Response
import java.io.IOException
import java.lang.IllegalStateException

class InvalidExternalServiceResponseException(val code: Int, val response: Response, msg: String) : IOException(msg)
class TokenHasExpiredException : IllegalStateException()