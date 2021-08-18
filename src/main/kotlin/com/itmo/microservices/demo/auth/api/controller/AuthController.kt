package com.itmo.microservices.demo.auth.api.controller

import com.itmo.microservices.demo.auth.api.model.AuthenticationRequest
import com.itmo.microservices.demo.auth.api.model.AuthenticationResult
import com.itmo.microservices.demo.auth.api.service.AuthService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authentication")
class AuthController(private val authService: AuthService) {

    @PostMapping
    fun authenticate(@RequestBody request: AuthenticationRequest): AuthenticationResult =
            authService.authenticate(request)

    @PostMapping("/refresh")
    fun refresh(authentication: Authentication): AuthenticationResult =
            authService.refresh(authentication)
}
