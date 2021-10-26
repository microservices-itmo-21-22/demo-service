package com.itmo.microservices.demo.bombardier.external

import com.shopify.promises.Promise
import com.shopify.promises.then
import okhttp3.*
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.boot.json.GsonJsonParser
import org.springframework.http.HttpStatus
import java.io.IOException
import java.lang.IllegalStateException
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime

class InvalidExternalServiceResponse(val code: Int, val response: Response, msg: String) : IOException(msg)
class TokenHasExpiredException() : IllegalStateException()

class ExternalServiceToken(accessToken: String, refreshToken: String) {
    private val tokenLifetime: Duration = Duration.ofMinutes(15)
    private val refreshTokenLifetime: Duration = Duration.ofDays(30)

    private var tokenCreatedAt = LocalDateTime.now()
    private var refreshTokenCreatedAt = LocalDateTime.now()

    private var _accessToken = accessToken
    var accessToken: String
        get() {
            if (isTokenExpired()) throw TokenHasExpiredException()
            return _accessToken
        }
        set (value) {
            tokenCreatedAt = LocalDateTime.now()
            _accessToken = value
        }

    private var _refreshToken = refreshToken
    var refreshToken: String
        get() {
            if (isRefreshTokenExpired()) throw TokenHasExpiredException()
            return _refreshToken
        }
        set (value) {
            refreshTokenCreatedAt = LocalDateTime.now()
            _refreshToken = value
        }

    fun isTokenExpired() = tokenCreatedAt + tokenLifetime > LocalDateTime.now()
    fun isRefreshTokenExpired() = refreshTokenCreatedAt + refreshTokenLifetime > LocalDateTime.now()
}

class ExternalServiceApiCommunicator(private val baseUrl: URL) {
    private val JSON = MediaType.parse("application/json; charset=utf-8")
    private val client = OkHttpClient()

    fun authenticate(username: String, password: String): Promise<ExternalServiceToken, IOException> {
        val body = JSONObject()
        body.put("username", username)
        body.put("password", password)

        return execute {
            url("/authentication")
            post(RequestBody.create(JSON, body.toString()))

        }.then {
            val resp = GsonJsonParser().parseMap(it.message())
            val accessToken = resp["accessToken"] as String
            val refreshToken = resp["refreshToken"] as String

            Promise.ofSuccess(ExternalServiceToken(accessToken, refreshToken))
        }
    }

    private fun reauthenticate(token: ExternalServiceToken): Promise<ExternalServiceToken, IOException> {
        return execute {
            url("/authentication")
            post(RequestBody.create(JSON, body.toString()))

        }.then {
            val resp = GsonJsonParser().parseMap(it.message())
            val accessToken = resp["accessToken"] as String
            val refreshToken = resp["refreshToken"] as String

            Promise.ofSuccess(ExternalServiceToken(accessToken, refreshToken))
        }
    }

    fun execute(builderContext: Request.Builder.() -> Unit): Promise<Response, IOException> {
        val requestBuilder = RequestBuilderWithBaseUrl(baseUrl)
        builderContext(requestBuilder)

        return Promise {
            client.newCall(requestBuilder.build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    reject(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code() != HttpStatus.OK.value()) {
                        reject(InvalidExternalServiceResponse(response.code(),
                            response,
                            "External service returned non-OK code: ${response.code()}"
                            )
                        )
                        return
                    }
                    resolve(response)
                }
            })
        }
    }

    fun executeWithAuth(credentials: ExternalServiceToken, builderContext: Request.Builder.() -> Unit): Promise<Response, IOException> {
        // todo check if auth is alive
        return execute {
            // todo pass token here
            builderContext(this)
        }
    }

    private class RequestBuilderWithBaseUrl(private val baseUrl: URL) : Request.Builder() {
        override fun url(url: URL): Request.Builder {
            return super.url(URL(baseUrl, url.toString()))
        }
    }
}