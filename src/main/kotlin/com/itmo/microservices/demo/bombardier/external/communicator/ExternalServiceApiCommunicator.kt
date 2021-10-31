package com.itmo.microservices.demo.bombardier.external.communicator

import okhttp3.*
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import java.io.IOException
import java.lang.IllegalStateException
import java.net.URL
import java.util.concurrent.ExecutorService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

open class ExternalServiceApiCommunicator(private val baseUrl: URL, private val executor: ExecutorService) {
    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
        fun JSONObject.toRequestBody() = RequestBody.create(JSON, this.toString())
    }

    private val client = OkHttpClient.Builder().run {
        dispatcher(Dispatcher(executor))
        build()
    }

    open suspend fun authenticate(username: String, password: String) = execute("/authentication") {
        val body = JSONObject().apply {
            put("username", username)
            put("password", password)
        }
        post(body.toRequestBody())

    }.run {
        mapper.readValue(body()!!.string(), TokenResponse::class.java).toExternalServiceToken(baseUrl)
    }

    protected suspend fun reauthenticate(token: ExternalServiceToken) = execute("/authentication/refresh") {
        assert(!token.isRefreshTokenExpired())
        header(HttpHeaders.AUTHORIZATION, "Bearer ${token.refreshToken}")
    }.run {
        mapper.readValue(body()!!.string(), TokenResponse::class.java).toExternalServiceToken(baseUrl)
    }

    suspend fun execute(url: String) = execute(url) {}

    suspend fun execute(url: String, builderContext: Request.Builder.() -> Unit): Response {
        val requestBuilder = RequestBuilderWithBaseUrl(baseUrl).apply {
            _url(url)
            builderContext(this)
        }

        return suspendCoroutine {
            client.newCall(requestBuilder.build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code() == HttpStatus.OK.value()) {
                        it.resume(response)
                        return
                    }
                    it.resumeWithException(InvalidExternalServiceResponseException(
                        response.code(),
                        response,
                        "External service returned non-OK code: ${response.code()}"
                    ))
                }
            })
        }
    }

    suspend fun executeWithAuth(url: String, credentials: ExternalServiceToken) = executeWithAuth(url, credentials) {}

    suspend fun executeWithAuth(url: String, credentials: ExternalServiceToken, builderContext: Request.Builder.() -> Unit): Response {
        if (credentials.isTokenExpired()) {
            reauthenticate(credentials)
        }

        return execute(url) {
            header(HttpHeaders.AUTHORIZATION, "Bearer ${credentials.accessToken}")
            builderContext(this)
        }
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

