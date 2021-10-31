package com.itmo.microservices.demo.bombardier.external.communicator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.net.URL
import java.util.concurrent.ExecutorService

class UserAwareExternalServiceApiCommunicator(baseUrl: URL, ex: ExecutorService) : ExtendedExternalServiceApiCommunicator(baseUrl, ex) {
    private val usersMap = mutableMapOf<String, ExternalServiceToken>()

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

    suspend fun runSessionRefresher(executor: ExecutorService) = CoroutineScope(executor.asCoroutineDispatcher()).launch {
        while (true) {
            for ((username, token) in usersMap) {
                if (token.isTokenExpired()) {
                    val newToken = reauthenticate(token)
                    usersMap[username] = newToken
                }
            }
        }
    }
}