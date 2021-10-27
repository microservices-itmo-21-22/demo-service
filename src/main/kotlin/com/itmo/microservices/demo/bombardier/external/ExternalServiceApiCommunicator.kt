package com.itmo.microservices.demo.bombardier.external

import com.shopify.promises.Promise
import com.shopify.promises.then
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.internal.Util.EMPTY_REQUEST
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import java.io.IOException
import java.lang.IllegalStateException
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime

// responses
@Serializable
data class TokenResponse(val accessToken: String?, val refreshToken: String)
fun TokenResponse.toExternalServiceToken(service: URL) = ExternalServiceToken(service, accessToken!!, refreshToken)

class InvalidExternalServiceResponse(val code: Int, val response: Response, msg: String) : IOException(msg)
class TokenHasExpiredException : IllegalStateException()

class ExternalServiceToken(val service: URL, accessToken: String, refreshToken: String) {
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

    fun isTokenExpired() = tokenCreatedAt + tokenLifetime <= LocalDateTime.now()
    fun isRefreshTokenExpired() = refreshTokenCreatedAt + refreshTokenLifetime <= LocalDateTime.now()

    override fun toString(): String {
        return "[$service: access $_accessToken (alive ${!isTokenExpired()}), refresh $_refreshToken (alive ${!isRefreshTokenExpired()})]"
    }
}

class ExternalServiceApiCommunicator(private val baseUrl: URL) {
    private val JSON = MediaType.parse("application/json; charset=utf-8")
    private val client = OkHttpClient()

    fun authenticate(username: String, password: String): Promise<ExternalServiceToken, IOException> {
        val body = JSONObject().apply {
            put("username", username)
            put("password", password)
        }

        return execute {
            url("/authentication")
            post(RequestBody.create(JSON, body.toString()))

        }.then {
            Promise.ofSuccess(Json.decodeFromString<TokenResponse>(it.message()).toExternalServiceToken(baseUrl))
        }
    }

    private fun reauthenticate(token: ExternalServiceToken): Promise<ExternalServiceToken, IOException> {
        return execute {
            url("/authentication")
            header(HttpHeaders.AUTHORIZATION, token.refreshToken)
            post(EMPTY_REQUEST)

        }.then {
            Promise.ofSuccess(Json.decodeFromString<TokenResponse>(it.message()).toExternalServiceToken(baseUrl))
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
                    if (response.code() == HttpStatus.OK.value()) {
                        resolve(response)
                        return
                    }
                    reject(InvalidExternalServiceResponse(response.code(),
                        response,
                        "External service returned non-OK code: ${response.code()}"
                    )
                    )
                }
            })
        }
    }

    fun executeWithAuth(credentials: ExternalServiceToken, builderContext: Request.Builder.() -> Unit): Promise<Response, IOException> {
        if (credentials.isTokenExpired()) {
            reauthenticate(credentials)
        }

        return execute {
            header(HttpHeaders.AUTHORIZATION, credentials.refreshToken)
            builderContext(this)
        }
    }

    private class RequestBuilderWithBaseUrl(private val baseUrl: URL) : Request.Builder() {
        override fun url(url: URL): Request.Builder {
            return super.url(URL(baseUrl, url.toString()))
        }

        override fun url(url: String): Request.Builder {
            return super.url(URL(baseUrl, url))
        }

        // todo: Other methods call this method. It's better to override this one, but I don't know how..
        override fun url(url: HttpUrl): Request.Builder {
            return super.url(url)
        }
    }
}