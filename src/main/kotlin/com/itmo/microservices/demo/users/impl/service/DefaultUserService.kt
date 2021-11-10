package com.itmo.microservices.demo.users.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.users.api.messaging.UserCreatedEvent
import com.itmo.microservices.demo.users.api.messaging.UserDeletedEvent
import com.itmo.microservices.demo.users.api.service.UserService
import com.itmo.microservices.demo.users.impl.entity.AppUser
import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.AuthenticationRequest
import com.itmo.microservices.demo.users.api.model.AuthenticationResult
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import com.itmo.microservices.demo.users.impl.repository.UserRepository
import com.itmo.microservices.demo.users.impl.util.toModel
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Suppress("UnstableApiUsage")
@Service
class DefaultUserService(private val userRepository: UserRepository,
                         private val passwordEncoder: PasswordEncoder,
                         private val eventBus: EventBus,
                         private val tokenManager: JwtTokenManager
                         ): UserService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun getUserModel(name: String): AppUserModel? = this
            .getUser(name)?.toModel()

    override fun getUser(name: String): AppUser? = userRepository
            .findByName(name)

    override fun getUser(userId: UUID): AppUser? = userRepository.findById(userId)

    override fun registerUser(request: RegistrationRequest): AppUser? {
        val userEntity = userRepository.save(request.toEntity())
        eventBus.post(UserCreatedEvent(userEntity.toModel()))
        //eventLogger.info(UserServiceNotableEvents.I_USER_CREATED, userEntity.username)
        return userEntity
    }

    override fun getAccountData(requester: UserDetails): AppUserModel =
            userRepository.findByName(requester.username)?.toModel() ?:
            throw NotFoundException("User ${requester.username} not found")

    override fun deleteUser(user: UserDetails) {
        runCatching {
            userRepository.deleteById(user.username)
        }.onSuccess {
            eventBus.post(UserDeletedEvent(user.username))
            //eventLogger.info(UserServiceNotableEvents.I_USER_DELETED, user.username)
        }.onFailure {
            throw NotFoundException("User ${user.username} not found", it)
        }
    }

    fun RegistrationRequest.toEntity(): AppUser =
        AppUser(
            name = this.name,
            password = passwordEncoder.encode(this.password)
        )

    override fun authenticate(request: AuthenticationRequest): AuthenticationResult {
        val user = getUserModel(request.name)
            ?: throw NotFoundException("User with name ${request.name} not found")

        if (!passwordEncoder.matches(request.password, user.password))
            throw AccessDeniedException("Invalid password")

        val accessToken = tokenManager.generateToken(user.userDetails())
        val refreshToken = tokenManager.generateRefreshToken(user.userDetails())
        return AuthenticationResult(accessToken, refreshToken)
    }

    override fun refresh(authentication: Authentication): AuthenticationResult {
        val refreshToken = authentication.credentials as String
        val principal = authentication.principal as UserDetails
        val accessToken = tokenManager.generateToken(principal)
        return AuthenticationResult(accessToken, refreshToken)
    }
}
