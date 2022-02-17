package com.itmo.microservices.demo.bombardier.external.communicator

import okhttp3.*
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import java.io.IOException
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CachedResponseBody internal constructor(_body: ResponseBody) {
    private val string: String
    init {
        string = _body.string()
    }
    fun string() = string
}

class TrimmedResponse private constructor(private val body: CachedResponseBody, private val code: Int, private val req: Request) {
    companion object {
        fun fromResponse(resp: Response): TrimmedResponse {
            return TrimmedResponse(CachedResponseBody(resp.body()!!), resp.code(), resp.request()).apply {
                resp.close()
            }
        }
    }
    fun body() = body
    fun code() = code
    fun request() = req
}

open class ExternalServiceApiCommunicator(private val baseUrl: URL, private val executor: ExecutorService) {
    init {
        Logger.getLogger(OkHttpClient::class.java.name).level = Level.FINE
    }
    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
        fun JSONObject.toRequestBody() = RequestBody.create(JSON, this.toString())
    }

    private val client = OkHttpClient.Builder().run {
        dispatcher(Dispatcher(executor))
        build()
    }

    open suspend fun authenticate(username: String, password: String) = execute("/authentication") {
        jsonPost(
            "name" to username,
            "password" to password
        )

    }.run {
        val resp = body().string()
        mapper.readValue(resp, TokenResponse::class.java).toExternalServiceToken(baseUrl)
    }

    protected suspend fun reauthenticate(token: ExternalServiceToken) = execute("/authentication/refresh") {
        assert(!token.isRefreshTokenExpired())
        header(HttpHeaders.AUTHORIZATION, "Bearer ${token.refreshToken}")
    }.run {
        mapper.readValue(body().string(), TokenResponse::class.java).toExternalServiceToken(baseUrl)
    }

    suspend fun execute(url: String) = execute(url) {}

    suspend fun execute(url: String, builderContext: CustomRequestBuilder.() -> Unit): TrimmedResponse {
        val requestBuilder = CustomRequestBuilder(baseUrl).apply {
            _url(url)
            builderContext(this)
        }

        return suspendCoroutine {
            client.newCall(requestBuilder.build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (HttpStatus.Series.resolve(response.code()) == HttpStatus.Series.SUCCESSFUL) {
                        it.resume(TrimmedResponse.fromResponse(response))
                        return
                    }
                    it.resumeWithException(InvalidExternalServiceResponseException(
                        response.code(),
                        response,
                        "${response.request().method()} ${response.request().url()} External service returned non-OK code: ${response.code()}\n\n${response.body()?.string()}"
                    ))
                }
            })
        }
    }

    suspend fun executeWithAuth(url: String, credentials: ExternalServiceToken) = executeWithAuth(url, credentials) {}

    suspend fun executeWithAuth(url: String, credentials: ExternalServiceToken, builderContext: CustomRequestBuilder.() -> Unit): TrimmedResponse {
        if (credentials.isTokenExpired()) {
            reauthenticate(credentials)
        }

        return execute(url) {
            header(HttpHeaders.AUTHORIZATION, "Bearer ${credentials.accessToken}")
            builderContext(this)
        }
    }



    class CustomRequestBuilder(private val baseUrl: URL) : Request.Builder() {
        companion object {
            val emptyBody = RequestBody.create(null, ByteArray(0))
        }
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

        fun jsonPost(ctx: JSONObject.() -> Unit) {
            val obj = JSONObject()
            ctx(obj)
            post(obj.toRequestBody())
        }

        fun jsonPost(vararg items: Pair<String, String>) {
            post(JSONObject().withItems(*items).toRequestBody())
        }

        fun post() {
            post(emptyBody)
        }

        fun put() {
            put(emptyBody)
        }
    }
}

