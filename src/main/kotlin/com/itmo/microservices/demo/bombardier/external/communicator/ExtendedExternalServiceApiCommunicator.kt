package com.itmo.microservices.demo.bombardier.external.communicator

import com.shopify.promises.Promise
import com.shopify.promises.then
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Request
import java.net.URL

class ExtendedExternalServiceApiCommunicator(baseUrl: URL) : ExternalServiceApiCommunicator(baseUrl) {
    inline fun <reified T> executeWithDeserialize(url: String) = executeWithDeserialize<T>(url) {}
    inline fun <reified T> executeWithAuthAndDeserialize(url: String, credentials: ExternalServiceToken)
        = executeWithAuthAndDeserialize<T>(url, credentials) {}

    inline fun <reified T> executeWithDeserialize(url: String, noinline builderContext: Request.Builder.() -> Unit) =
        execute(url, builderContext).then {
            Promise.ofSuccess(Json.decodeFromString<T>(it.body()!!.string()))
        }

    inline fun <reified T> executeWithAuthAndDeserialize(
        url: String,
        credentials: ExternalServiceToken,
        noinline builderContext: Request.Builder.() -> Unit
    ) =
        executeWithAuth(url, credentials, builderContext).then {
            Promise.ofSuccess(Json.decodeFromString<T>(it.body()!!.string()))
        }
}