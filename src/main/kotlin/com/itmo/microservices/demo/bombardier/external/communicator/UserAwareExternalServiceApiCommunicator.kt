package com.itmo.microservices.demo.bombardier.external.communicator

import com.itmo.microservices.demo.bombardier.external.knownServices.ServiceDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService

class UserAwareExternalServiceApiCommunicator(descriptor: ServiceDescriptor, ex: ExecutorService) :
    ExtendedExternalServiceApiCommunicator(
        descriptor, ex
    ) {
    private val usersMap = mutableMapOf<String, ExternalServiceToken>()

    private val refresherCoroutine = CoroutineScope(ex.asCoroutineDispatcher()).launch {
        //runSessionRefresher()
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
        throw Exception("Too many attempts")
    }

    private suspend fun runSessionRefresherImpl(currentRetries: Int): Int {
        while (true) {
            for ((username, token) in usersMap) {
                if (token.isTokenExpired()) {
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
}