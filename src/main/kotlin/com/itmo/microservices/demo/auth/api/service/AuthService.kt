package com.itmo.microservices.demo.auth.api.service

import com.itmo.microservices.demo.auth.api.model.AuthenticationRequest
import com.itmo.microservices.demo.auth.api.model.AuthenticationResult
import org.springframework.security.core.Authentication

interface AuthService {
    fun authenticate(request: AuthenticationRequest): AuthenticationResult
    fun refresh(authentication: Authentication): AuthenticationResult
}