package com.itmo.microservices.demo.bombardier.external.communicator

import java.net.URL
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

const val tokenLifetimeSec = 15 * 60
const val refreshTokenLifetimeSec = 30 * 24 * 60 * 60

class ExternalServiceToken(val service: URL, accessToken: String, refreshToken: String) {
    var lastAccessedAt = Instant.now().epochSecond
        private set



    private var tokenCreatedAt = Instant.now().epochSecond
    private var refreshTokenCreatedAt = tokenCreatedAt

    private var _accessToken = accessToken
    var accessToken: String
        get() {
            lastAccessedAt = Instant.now().epochSecond
            if (isTokenExpired()) throw TokenHasExpiredException()
            return _accessToken
        }
        set(value) {
            lastAccessedAt = Instant.now().epochSecond
            tokenCreatedAt = Instant.now().epochSecond
            _accessToken = value
        }

    private var _refreshToken = refreshToken
    var refreshToken: String
        get() {
            lastAccessedAt = Instant.now().epochSecond
            if (isRefreshTokenExpired()) throw TokenHasExpiredException()
            return _refreshToken
        }
        set(value) {
            lastAccessedAt = Instant.now().epochSecond
            refreshTokenCreatedAt = Instant.now().epochSecond
            _refreshToken = value
        }

    fun isTokenExpired() = tokenCreatedAt + tokenLifetimeSec <= Instant.now().epochSecond
    fun isTokenExpiringSoon() = tokenCreatedAt + tokenLifetimeSec - (15*60) <= Instant.now().epochSecond
    fun isRefreshTokenExpired() = refreshTokenCreatedAt + refreshTokenLifetimeSec <= Instant.now().epochSecond

    // считаем что токен до сих пор используется если к нему обращались в течении 24 часов
    fun isNotStale() = true//lastAccessedAt + 86400 >= Instant.now().epochSecond

    override fun toString(): String {
        return "[$service: access $_accessToken (alive ${!isTokenExpired()}), refresh $_refreshToken (alive ${!isRefreshTokenExpired()})]"
    }
}