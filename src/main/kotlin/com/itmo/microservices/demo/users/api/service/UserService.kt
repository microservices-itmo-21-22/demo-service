package com.itmo.microservices.demo.users.api.service

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.AuthenticationRequest
import com.itmo.microservices.demo.users.api.model.AuthenticationResult
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import com.itmo.microservices.demo.users.impl.entity.AppUser
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface UserService {
    fun getUserModel(name: String): AppUserModel?
    fun getUser(name: String): AppUser?
    fun getUser(userId: UUID): AppUser?
    fun registerUser(request: RegistrationRequest)
    fun getAccountData(requester: UserDetails): AppUserModel
    fun deleteUser(user: UserDetails)
    fun authenticate(request: AuthenticationRequest): AuthenticationResult
    fun refresh(authentication: Authentication): AuthenticationResult
}