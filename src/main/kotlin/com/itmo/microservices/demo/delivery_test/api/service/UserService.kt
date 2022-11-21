package com.itmo.microservices.demo.users.api.service

import com.itmo.microservices.demo.users.api.model.UserDTO
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import java.util.UUID

interface UserService {
    fun findUser(id: UUID): UserDTO?
    fun registerUser(request: RegistrationRequest): UserDTO
    fun getAccountData(id: UUID): UserDTO
    fun findUserbyName(name: String): UserDTO?

}