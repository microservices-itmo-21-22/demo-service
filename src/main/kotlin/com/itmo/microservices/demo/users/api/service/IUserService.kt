package com.itmo.microservices.demo.users.api.service

import com.itmo.microservices.demo.users.api.model.AuthenticationRequest
import com.itmo.microservices.demo.users.api.model.AuthenticationResult
import com.itmo.microservices.demo.users.api.model.UserModel
import com.itmo.microservices.demo.users.api.model.UserResponseDto
import org.springframework.security.core.Authentication

interface IUserService {
    fun addUser(userModel: UserModel): UserResponseDto
    fun getUserById(id: Int): UserResponseDto?
    fun authUser(request: AuthenticationRequest): AuthenticationResult
    fun refreshToken(authentication: Authentication): AuthenticationResult
}