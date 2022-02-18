package com.itmo.microservices.demo.users.api.service

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface UserService {
    fun findUser(username: String): AppUserModel?
    fun registerUser(request: RegistrationRequest): AppUserModel
    fun getAccountData(requester: UserDetails?, userId: UUID): AppUserModel
}