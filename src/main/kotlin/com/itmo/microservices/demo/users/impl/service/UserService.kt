package com.itmo.microservices.demo.users.impl.service

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.users.api.model.AuthenticationRequest
import com.itmo.microservices.demo.users.api.model.AuthenticationResult
import com.itmo.microservices.demo.users.api.model.UserModel
import com.itmo.microservices.demo.users.api.model.UserResponseDto
import com.itmo.microservices.demo.users.api.service.IUserService
import com.itmo.microservices.demo.users.impl.entity.User
import com.itmo.microservices.demo.users.impl.logging.UserServiceNotableEvents
import com.itmo.microservices.demo.users.impl.repository.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository,
                  private val tokenManager: JwtTokenManager,
                  private val passwordEncoder: PasswordEncoder
): IUserService{

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    private fun findUser(name: String): UserModel = userRepository
        .findUserByName(name).toModel()

    override fun addUser(userModel: UserModel): UserResponseDto {
        val user = userRepository.save(userModel.toEntity())
        eventLogger.info(UserServiceNotableEvents.I_USER_CREATED, user.name)
        return UserResponseDto(user.id!!, user.name!!)
    }

    override fun getUserById(id: Int): UserResponseDto {
        val user = userRepository.findById(id)
        return UserResponseDto(user.get().id!!, user.get().name!!)
    }

    override fun authUser(request: AuthenticationRequest): AuthenticationResult {
        val user = findUser(request.username)

        if (request.password != user.password)
            throw AccessDeniedException("Invalid password")

        val accessToken = tokenManager.generateToken(user.userDetails())
        val refreshToken = tokenManager.generateRefreshToken(user.userDetails())
        return AuthenticationResult(accessToken, refreshToken)
    }

    override fun refreshToken(authentication: Authentication): AuthenticationResult {
        val refreshToken = authentication.credentials as String
        val principal = authentication.principal as UserDetails
        val accessToken = tokenManager.generateToken(principal)
        return AuthenticationResult(accessToken, refreshToken)
    }

    private fun UserModel.toEntity() = User(this.name, this.password, this.status)

    private fun User.toModel() = UserModel(this.name!!, this.password!!, this.status!!)
}
