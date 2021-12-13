package com.itmo.microservices.demo.users.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.users.api.messaging.UserCreatedEvent
import com.itmo.microservices.demo.users.api.service.UserService
import com.itmo.microservices.demo.users.impl.entity.AppUser
import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import com.itmo.microservices.demo.users.impl.logging.UserServiceNotableEvents
import com.itmo.microservices.demo.users.impl.repository.UserRepository
import com.itmo.microservices.demo.users.impl.util.toModel
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.NotAcceptableStatusException
import java.util.*

@Suppress("UnstableApiUsage")
@Service
class DefaultUserService(private val userRepository: UserRepository,
                         private val passwordEncoder: PasswordEncoder,
                         private val eventBus: EventBus
                         ): UserService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun findUser(username: String): AppUserModel? = userRepository
            .findByName(username)?.toModel()

    override fun registerUser(request: RegistrationRequest): AppUserModel {
        //There is no prevention of duplicate user registrations
        val aUser = findUser(request.name)
        if(aUser!=null)throw NotAcceptableStatusException("User already exists")
        val userEntity = userRepository.save(request.toEntity())
        eventBus.post(UserCreatedEvent(userEntity.toModel()))

        if(::eventLogger.isInitialized){
            eventLogger.info(UserServiceNotableEvents.I_USER_CREATED, userEntity.name)
        }

        return userEntity.toModel()
    }

    override fun getAccountData(requester: UserDetails?,uuid: UUID): AppUserModel =
            userRepository.findById(uuid)?.toModel() ?:
            throw NotFoundException("User ${requester?.username} not found")

    override fun deleteAllUsers() {
        userRepository.deleteAll()
    }

    fun RegistrationRequest.toEntity(): AppUser =
        AppUser(
            name = this.name,
            password = passwordEncoder.encode(this.password),

        )
}
