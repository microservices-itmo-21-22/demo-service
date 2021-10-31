package com.itmo.microservices.demo.bombardier.external.communicator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.concurrent.ExecutorService

class UserAwareExternalServiceApiCommunicator(baseUrl: URL, ex: ExecutorService) : ExtendedExternalServiceApiCommunicator(baseUrl, ex) {
    private val usersMap = mutableMapOf<String, ExternalServiceToken>()

    private val logger = LoggerFactory.getLogger(this.javaClass)

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

    suspend fun runSessionRefresher() {
        var refresherLast = runSessionRefresherImpl(0)
        while (refresherLast < 10) {
            refresherLast = runSessionRefresherImpl(refresherLast)
        }
    }

    private suspend fun runSessionRefresherImpl(currentRetries: Int): Int {
        while (true) {
            for ((username, token) in usersMap) {
                if (token.isTokenExpired()) {
                    try {
                        val newToken = reauthenticate(token)
                        usersMap[username] = newToken
                    }
                    catch (t: Throwable) {
                        logger.error("Failed to refresh acc $username with token $token", t)
                        return currentRetries + 1
                    }
                }
            }
        }
    }
}