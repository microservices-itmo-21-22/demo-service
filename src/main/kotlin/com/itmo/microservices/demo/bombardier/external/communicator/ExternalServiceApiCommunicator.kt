package com.itmo.microservices.demo.bombardier.external.communicator

import com.shopify.promises.Promise
import com.shopify.promises.then
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import java.io.IOException
import java.lang.IllegalStateException
import java.net.URL

open class ExternalServiceApiCommunicator(private val baseUrl: URL) {
    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
        fun JSONObject.toRequestBody() = RequestBody.create(JSON, this.toString())
    }

    private val client = OkHttpClient()

    fun authenticate(username: String, password: String) = execute("/authentication") {
        val body = JSONObject().apply {
            put("username", username)
            put("password", password)
        }
        post(body.toRequestBody())

    }.then {
        Promise.ofSuccess(Json.decodeFromString<TokenResponse>(it.body()!!.string()).toExternalServiceToken(baseUrl))
    }

    private fun reauthenticate(token: ExternalServiceToken) = execute("/authentication/refresh") {
        assert(!token.isRefreshTokenExpired())
        header(HttpHeaders.AUTHORIZATION, "Bearer ${token.refreshToken}")
    }.then {
        Promise.ofSuccess(Json.decodeFromString<TokenResponse>(it.body()!!.string()).toExternalServiceToken(baseUrl))
    }

    fun execute(url: String) = execute(url) {}

    fun execute(url: String, builderContext: Request.Builder.() -> Unit): Promise<Response, IOException> {
        val requestBuilder = RequestBuilderWithBaseUrl(baseUrl).apply {
            _url(url)
            builderContext(this)
        }

        return Promise {
            client.newCall(requestBuilder.build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    reject(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code() == HttpStatus.OK.value()) {
                        resolve(response)
                        return
                    }
                    reject(
                        InvalidExternalServiceResponseException(
                            response.code(),
                            response,
                            "External service returned non-OK code: ${response.code()}"
                        )
                    )
                }
            })
        }
    }

    fun executeWithAuth(url: String, credentials: ExternalServiceToken) = executeWithAuth(url, credentials) {}

    fun executeWithAuth(url: String, credentials: ExternalServiceToken, builderContext: Request.Builder.() -> Unit) =
        execute(url) {
            if (credentials.isTokenExpired()) {
                reauthenticate(credentials)
            }

            header(HttpHeaders.AUTHORIZATION, "Bearer ${credentials.accessToken}")
            builderContext(this)
        }


    private class RequestBuilderWithBaseUrl(private val baseUrl: URL) : Request.Builder() {
        fun _url(url: String) = super.url(URL(baseUrl, url))

        /**
         * Don't call this method in builder
         */
        @Deprecated("Not allowed to use")
        override fun url(url: URL) = throw IllegalStateException("Not allowed to call this function")

        /**
         * Don't call this method in builder
         */
        @Deprecated("Not allowed to use")
        override fun url(url: String) = throw IllegalStateException("Not allowed to call this function")

    }
}

