package com.itmo.microservices.demo.users.api.controller

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.AuthenticationRequest
import com.itmo.microservices.demo.users.api.model.AuthenticationResult
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import com.itmo.microservices.demo.users.api.service.UserService
import com.itmo.microservices.demo.users.impl.entity.AppUser
import com.itmo.microservices.demo.users.impl.util.toModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.websocket.server.PathParam

@RestController
class UserController(private val userService: UserService) {

    @PostMapping("/users")
    @Operation(
        summary = "Register new user",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ]
    )
    fun register(@RequestBody request: RegistrationRequest): AppUser? = userService.registerUser(request)

    @GetMapping("/{user_id}")
    @Operation(
        summary = "Get user by id",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getUserById(@PathVariable("user_id") userId: UUID): AppUser? = userService.getUser(userId)

    @GetMapping("/users/me")
    @Operation(
        summary = "Get current user info",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getAccountData(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails): AppUserModel =
            userService.getAccountData(user)

    @DeleteMapping("/users/me")
    @Operation(
        summary = "Delete current user",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteCurrentUser(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails) =
            userService.deleteUser(user)

    @PostMapping("/authentication")
    @Operation(
        summary = "Authenticate",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()]),
            ApiResponse(description = "Invalid password", responseCode = "403", content = [Content()])
        ]
    )
    fun authenticate(@RequestBody request: AuthenticationRequest): AuthenticationResult =
        userService.authenticate(request);

    @PostMapping("/authentication/refresh")
    @Operation(
        summary = "Refresh authentication",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Authentication error", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun refresh(authentication: Authentication): AuthenticationResult =
        userService.refresh(authentication)
}