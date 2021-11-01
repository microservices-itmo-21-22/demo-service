package com.itmo.microservices.demo.notifications.impl.service

import com.itmo.microservices.demo.notifications.api.service.NotificationService
import com.itmo.microservices.demo.notifications.impl.entity.NotificationUser
import com.itmo.microservices.demo.notifications.impl.repository.NotificationUserRepository
import com.itmo.microservices.demo.users.api.model.UserModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StubNotificationService(private val userRepository: NotificationUserRepository) : NotificationService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(StubNotificationService::class.java)
    }

    override fun processNewUser(user: UserModel) {
        userRepository.save(modelToEntity(user))
        log.info("User ${user.name} was created & should be notified (but who cares)")
    }

    private fun modelToEntity(user: UserModel): NotificationUser =
        NotificationUser(name = user.name,)
}