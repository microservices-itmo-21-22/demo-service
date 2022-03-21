package com.itmo.microservices.demo.bombardier.external.communicator

import com.itmo.microservices.demo.bombardier.BombardierProperties
import com.itmo.microservices.demo.bombardier.ServiceDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService

private const val REFRESH_TIME_MS: Long = 1000L * (tokenLifetimeSec - 30)

class UserAwareExternalServiceApiCommunicator(descriptor: ServiceDescriptor, ex: ExecutorService, props: BombardierProperties) :
    ExtendedExternalServiceApiCommunicator(
        descriptor, ex, props
    ) {
    private val usersMap = mutableMapOf<String, ExternalServiceToken>()

    private val refresherCoroutine = CoroutineScope(ex.asCoroutineDispatcher()).launch {
        if (!props.authEnabled) return@launch
        runSessionRefresher()
    }

    override suspend fun authenticate(username: String, password: String): ExternalServiceToken {
        val availableToken = usersMap[username]
        if (availableToken != null && !availableToken.isTokenExpired()) {
            return availableToken
        }

        val auth = super.authenticate(username, password)
        usersMap[username] = auth
        return auth
    }

    fun getUserSession(username: String) = usersMap[username]

    private suspend fun runSessionRefresher() {
        var refresherLast = runSessionRefresherImpl(0)
        while (refresherLast < 10) {
            refresherLast = runSessionRefresherImpl(refresherLast)
        }
        throw Exception("Too many failed attempts to refresh token")
    }

    private suspend fun runSessionRefresherImpl(currentRetries: Int): Int {
        while (true) {
            delay(REFRESH_TIME_MS)
            for ((username, token) in usersMap.filter { it.value.isTokenExpiringSoon() && it.value.isNotStale() }) {
                try {
                    val newToken = reauthenticate(token)
                    usersMap[username] = newToken
                } catch (t: Throwable) {
                    logger.error("Failed to refresh acc $username with token $token", t)
                    return currentRetries + 1
                }
            }
        }
    }
}