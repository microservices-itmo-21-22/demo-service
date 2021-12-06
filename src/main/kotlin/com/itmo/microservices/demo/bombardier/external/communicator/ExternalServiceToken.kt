package com.itmo.microservices.demo.bombardier.external.communicator

import java.net.URL
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

class ExternalServiceToken(val service: URL, accessToken: String, refreshToken: String) {
    private val tokenLifetimeSec = 15 * 60
    private val refreshTokenLifetimeSec = 30 * 24 * 60 * 60

    private var tokenCreatedAt = Instant.now().epochSecond
    private var refreshTokenCreatedAt = tokenCreatedAt

    private var _accessToken = accessToken
    var accessToken: String
        get() {
            if (isTokenExpired()) throw TokenHasExpiredException()
            return _accessToken
        }
        set(value) {
            tokenCreatedAt = Instant.now().epochSecond
            _accessToken = value
        }

    private var _refreshToken = refreshToken
    var refreshToken: String
        get() {
            if (isRefreshTokenExpired()) throw TokenHasExpiredException()
            return _refreshToken
        }
        set(value) {
            refreshTokenCreatedAt = Instant.now().epochSecond
            _refreshToken = value
        }

    fun isTokenExpired() = tokenCreatedAt + tokenLifetimeSec <= Instant.now().epochSecond
    fun isRefreshTokenExpired() = refreshTokenCreatedAt + refreshTokenLifetimeSec <= Instant.now().epochSecond

    override fun toString(): String {
        return "[$service: access $_accessToken (alive ${!isTokenExpired()}), refresh $_refreshToken (alive ${!isRefreshTokenExpired()})]"
    }
}