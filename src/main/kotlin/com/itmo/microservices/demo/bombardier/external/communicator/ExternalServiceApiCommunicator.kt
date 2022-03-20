package com.itmo.microservices.demo.bombardier.external.communicator

import com.itmo.microservices.demo.bombardier.BombardierProperties
import com.itmo.microservices.demo.bombardier.ServiceDescriptor
import com.itmo.microservices.demo.common.logging.LoggerWrapper
import okhttp3.*
import org.slf4j.LoggerFactory
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import java.io.IOException
import java.net.URL
import java.time.Duration
import java.util.concurrent.ExecutorService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.itmo.microservices.demo.common.metrics.Metrics

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

open class ExternalServiceApiCommunicator(private val descriptor: ServiceDescriptor, private val executor: ExecutorService, private val props: BombardierProperties) {
    companion object {
        private val TIMEOUT = Duration.ofSeconds(20)
        private val JSON = MediaType.parse("application/json; charset=utf-8")
        fun JSONObject.toRequestBody() = RequestBody.create(JSON, this.toString())
    }

    val logger = LoggerWrapper(
        LoggerFactory.getLogger(ExternalServiceApiCommunicator::class.java),
        descriptor.name
    )

    private val client = OkHttpClient.Builder().run {
        dispatcher(Dispatcher(executor))
        callTimeout(TIMEOUT)
        build()
    }

    open suspend fun authenticate(username: String, password: String) = execute("authenticate", "/authentication") {
        jsonPost(
            "name" to username,
            "password" to password
        )

    }.run {
        val resp = body().string()
        mapper.readValue(resp, TokenResponse::class.java).toExternalServiceToken(descriptor.url)
    }

    protected suspend fun reauthenticate(token: ExternalServiceToken) = execute("reauthenticate", "/authentication/refresh") {
        assert(!token.isRefreshTokenExpired())
        post()
        header(HttpHeaders.AUTHORIZATION, "Bearer ${token.refreshToken}")
    }.run {
        mapper.readValue(body().string(), TokenResponse::class.java).toExternalServiceToken(descriptor.url)
    }

    suspend fun execute(method: String, url: String) = execute(method, url) {}

    suspend fun execute(method: String, url: String, builderContext: CustomRequestBuilder.() -> Unit): TrimmedResponse {
        val requestBuilder = CustomRequestBuilder(descriptor.url).apply {
            _url(url)
            builderContext(this)
        }
        val metrics = Metrics().withTags("service", this.descriptor.name, "method", method)
        return suspendCoroutine {
            val req = requestBuilder.build()
            logger.info("sending request to ${req.url().url()}")
            val startTime = System.currentTimeMillis()

            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val endTime = System.currentTimeMillis()
                    metrics.withTags("code", response.code().toString()).externalMethodDurationRecord(endTime-startTime)

                    if (HttpStatus.Series.resolve(response.code()) == HttpStatus.Series.SUCCESSFUL) {
                        it.resume(TrimmedResponse.fromResponse(response))
                        return
                    }
                    it.resumeWithException(
                        InvalidExternalServiceResponseException(
                            response.code(),
                            response,
                            "${response.request().method()} ${
                                response.request().url()
                            } External service returned non-OK code: ${response.code()}\n\n${response.body()?.string()}"
                        )
                    )
                }
            })
        }
    }

    suspend fun executeWithAuth(method: String, url: String, credentials: ExternalServiceToken) = executeWithAuth(method, url, credentials) {}

    suspend fun executeWithAuth(
        method: String,
        url: String,
        credentials: ExternalServiceToken,
        builderContext: CustomRequestBuilder.() -> Unit
    ): TrimmedResponse {
        return execute(method, url) {
            if (props.authEnabled) {
                header(HttpHeaders.AUTHORIZATION, "Bearer ${credentials.accessToken}")
            }
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