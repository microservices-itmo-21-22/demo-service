package com.itmo.microservices.demo.auth.api.controller

import com.itmo.microservices.demo.auth.api.model.AuthenticationRequest
import com.itmo.microservices.demo.auth.api.model.AuthenticationResult
import com.itmo.microservices.demo.auth.api.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authentication")
class AuthController(private val authService: AuthService) {

    @PostMapping
    @Operation(
        summary = "Authenticate",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()]),
            ApiResponse(description = "Invalid password", responseCode = "403", content = [Content()])
        ]
    )
    fun authenticate(@RequestBody request: AuthenticationRequest): AuthenticationResult =
            authService.authenticate(request)

    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh authentication",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Authentication error", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun refresh(authentication: Authentication): AuthenticationResult =
            authService.refresh(authentication)
}
