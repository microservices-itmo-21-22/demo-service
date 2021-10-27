package com.itmo.microservices.demo.bombardier.external.communicator

import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
data class TokenResponse(val accessToken: String?, val refreshToken: String)

fun TokenResponse.toExternalServiceToken(service: URL) = ExternalServiceToken(service, accessToken!!, refreshToken)