package com.itmo.microservices.demo.bombardier.external.communicator

import com.itmo.microservices.demo.bombardier.BombardierProperties
import com.itmo.microservices.demo.bombardier.ServiceDescriptor
import java.util.concurrent.ExecutorService

open class ExtendedExternalServiceApiCommunicator(descriptor: ServiceDescriptor, ex: ExecutorService, props: BombardierProperties) :
    ExternalServiceApiCommunicator(
        descriptor, ex, props
    ) {
    suspend inline fun <reified T> executeWithDeserialize(method: String, url: String) =
        executeWithDeserialize<T>(method, url) {}

    suspend inline fun <reified T> executeWithAuthAndDeserialize(method: String,url: String, credentials: ExternalServiceToken) =
        executeWithAuthAndDeserialize<T>(method, url, credentials) {}

    suspend inline fun <reified T> executeWithDeserialize(
        method: String,
        url: String,
        noinline builderContext: CustomRequestBuilder.() -> Unit
    ): T {
        val res = execute(method, url, builderContext)
        return try {
            readValueBombardier(res.body().string())
        } catch (t: BombardierMappingException) {
            throw t.exceptionWithUrl("${res.request().method()} ${res.request().url()}")
        }
    }

    suspend inline fun <reified T> executeWithAuthAndDeserialize(
        method: String,
        url: String,
        credentials: ExternalServiceToken,
        noinline builderContext: CustomRequestBuilder.() -> Unit
    ): T {
        val res = executeWithAuth(method, url, credentials, builderContext)
        return try {
            readValueBombardier(res.body().string())
        } catch (t: BombardierMappingException) {
            throw t.exceptionWithUrl("${res.request().method()} ${res.request().url()}")
        }
    }
}