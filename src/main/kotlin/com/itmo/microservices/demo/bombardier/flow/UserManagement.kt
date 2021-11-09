package com.itmo.microservices.demo.bombardier.flow

import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.external.User
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
class UserManagement(
    private val externalServiceApi: ExternalServiceApi
) {
    companion object {
        val log = LoggerFactory.getLogger(UserManagement::class.java)
    }

    private val userIdsByService = ConcurrentHashMap<String, MutableSet<UUID>>()
    private val externalServiceUserCache = ConcurrentHashMap<UUID, User>()

    suspend fun createUsersPool(service: String, numberOfUsers: Int): Set<UUID> {
        repeat(numberOfUsers) { index ->
            kotlin.runCatching {
                externalServiceApi.createUser("service-$service-user-$index-${System.currentTimeMillis()}")
            }.onSuccess { user ->
                userIdsByService
                    .computeIfAbsent(service) { ConcurrentHashMap.newKeySet() }
                    .add(user.id)

                externalServiceUserCache[user.id] = user
            }.onFailure {
                log.error("User has not been created", it)
            }
        }
        return userIdsByService[service].orEmpty() // todo make immutable
    }

    fun getRandomUserId(service: String): UUID {
        return userIdsByService[service]?.random()
            ?: throw IllegalStateException("There are no users for service $service")
    }
}