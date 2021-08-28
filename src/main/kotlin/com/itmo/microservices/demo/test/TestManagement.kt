package com.itmo.microservices.demo.test

import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class UserManagement(
    private val serviceApi: ServiceApi,
    private val internalAccountingService: InternalAccountingService
) {
    companion object {
        val log = LoggerFactory.getLogger(UserManagement::class.java)
    }

    private val userIdsByService = ConcurrentHashMap<String, MutableSet<UUID>>()
    private val externalServiceUserCache = ConcurrentHashMap<UUID, User>()

    suspend fun createUsersPool(service: String, numberOfUsers: Int): Set<UUID> {
        repeat(numberOfUsers) { index ->
            kotlin.runCatching {
                serviceApi.createUser("service-$service-user-$index-${System.currentTimeMillis()}", Int.MAX_VALUE)
            }.onSuccess { user ->
                internalAccountingService.initUserCredits(user.id, Int.MAX_VALUE)

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

class InternalAccountingService {
    private val usersCredits = ConcurrentHashMap<UUID, AtomicInteger>()

    fun initUserCredits(userId: UUID, credit: Amount) {
        usersCredits[userId] = AtomicInteger(credit)
    }

    fun spend(userId: UUID, credit: Amount) {
        usersCredits[userId]?.addAndGet(-credit)
            ?: throw IllegalArgumentException("There is no credit for user $userId")
    }

    fun get(userId: UUID) {
        usersCredits[userId]?.get() ?: throw IllegalArgumentException("There is no credit for user $userId")
    }

    fun refund(userId: UUID, credit: Amount) {
        usersCredits[userId]?.addAndGet(credit) ?: throw IllegalArgumentException("There is no credit for user $userId")
    }
}