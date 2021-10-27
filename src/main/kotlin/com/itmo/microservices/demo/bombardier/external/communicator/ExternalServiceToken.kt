package com.itmo.microservices.demo.bombardier.external.communicator

import java.net.URL
import java.time.Duration
import java.time.LocalDateTime

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
        set(value) {
            tokenCreatedAt = LocalDateTime.now()
            _accessToken = value
        }

    private var _refreshToken = refreshToken
    var refreshToken: String
        get() {
            if (isRefreshTokenExpired()) throw TokenHasExpiredException()
            return _refreshToken
        }
        set(value) {
            refreshTokenCreatedAt = LocalDateTime.now()
            _refreshToken = value
        }

    fun isTokenExpired() = tokenCreatedAt + tokenLifetime <= LocalDateTime.now()
    fun isRefreshTokenExpired() = refreshTokenCreatedAt + refreshTokenLifetime <= LocalDateTime.now()

    override fun toString(): String {
        return "[$service: access $_accessToken (alive ${!isTokenExpired()}), refresh $_refreshToken (alive ${!isRefreshTokenExpired()})]"
    }
}