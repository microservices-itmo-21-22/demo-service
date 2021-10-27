package com.itmo.microservices.demo.bombardier.external.communicator

import com.shopify.promises.Promise
import com.shopify.promises.completeOn
import com.shopify.promises.then
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL
import java.util.concurrent.ExecutorService

class UserAwareExternalServiceApiCommunicator(baseUrl: URL) : ExtendedExternalServiceApiCommunicator(baseUrl) {
    private val usersMap = mutableMapOf<String, ExternalServiceToken>()

    override fun authenticate(username: String, password: String): Promise<ExternalServiceToken, IOException> {
        usersMap[username]?.let {
            return Promise.ofSuccess(it)
        }

        return super.authenticate(username, password).then {
            usersMap[username] = it
            Promise.ofSuccess(it)
        }
    }

    fun getUserSession(username: String) = usersMap[username]

    fun runSessionRefresher(executor: ExecutorService) = CoroutineScope(executor.asCoroutineDispatcher()).launch {
        while (true) {
            for ((username, token) in usersMap) {
                if (token.isTokenExpired()) {
                    reauthenticate(token).completeOn(executor).whenComplete { result ->
                        (result as? Promise.Result.Success)?.let {
                            usersMap[username] = it.value
                        }
                    }
                }
            }
        }
    }
}