package com.itmo.microservices.demo.bombardier.external.communicator

import okhttp3.Request
import java.net.URL
import java.util.concurrent.ExecutorService

open class ExtendedExternalServiceApiCommunicator(baseUrl: URL, ex: ExecutorService) : ExternalServiceApiCommunicator(baseUrl, ex) {
    suspend inline fun <reified T> executeWithDeserialize(url: String) = executeWithDeserialize<T>(url) {}
    suspend inline fun <reified T> executeWithAuthAndDeserialize(url: String, credentials: ExternalServiceToken)
        = executeWithAuthAndDeserialize<T>(url, credentials) {}

    suspend inline fun <reified T> executeWithDeserialize(url: String, noinline builderContext: CustomRequestBuilder.() -> Unit): T =
        mapper.readValue(execute(url, builderContext).body()!!.string(), T::class.java)

    suspend inline fun <reified T> executeWithAuthAndDeserialize(
        url: String,
        credentials: ExternalServiceToken,
        noinline builderContext: CustomRequestBuilder.() -> Unit
    ): T =
        mapper.readValue(executeWithAuth(url, credentials, builderContext).body()!!.string(), T::class.java)
}