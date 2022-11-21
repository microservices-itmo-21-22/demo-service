package com.itmo.microservices.demo.users.impl.service

import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.users.api.service.UserService
import com.itmo.microservices.demo.users.impl.entity.User
import com.itmo.microservices.demo.users.api.model.UserDTO
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import com.itmo.microservices.demo.users.impl.repository.UserRepository
import com.itmo.microservices.demo.users.impl.util.toModel
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Suppress("UnstableApiUsage")
@Service
class DefaultUserService(private val userRepository: UserRepository,
                         private val passwordEncoder: PasswordEncoder
                        ): UserService {
//                         private val eventBus: EventBus


//    @InjectEventLogger
//    private lateinit var eventLogger: EventLogger

    override fun findUser(id: UUID): UserDTO? = userRepository
            .findAppUserById(id)?.toModel()

    override fun findUserbyName(name: String): UserDTO? = userRepository
        .findAppUserByName(name)?.toModel()

    override fun registerUser(request: RegistrationRequest): UserDTO {
        val userEntity = userRepository.save(request.toEntity())
        return userEntity.toModel()
    }

    override fun getAccountData(id: UUID): UserDTO =
            findUser(id)
                ?: throw NotFoundException("User $id not found")


    fun RegistrationRequest.toEntity(): User =
        User(id = UUID.randomUUID(),
            name = this.name,
            password = passwordEncoder.encode(this.password)
        )
}
