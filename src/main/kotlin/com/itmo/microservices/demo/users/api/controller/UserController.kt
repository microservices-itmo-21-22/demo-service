package com.itmo.microservices.demo.users.api.controller

import com.itmo.microservices.demo.users.api.model.*
import com.itmo.microservices.demo.users.api.service.IUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: IUserService) {

    @PostMapping
    @Operation(
        summary = "Register new user",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ]
    )
    fun addUser(@RequestBody request: UserRequestDto): UserResponseDto {
        return userService.addUser(request.toModel())
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by id",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getUserById(@PathVariable(value = "id") id: Int): UserResponseDto {
        return userService.getUserById(id)
    }

    @PostMapping("/auth")
    @Operation(
        summary = "Authenticate",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()]),
            ApiResponse(description = "Invalid password", responseCode = "403", content = [Content()])
        ]
    )
    fun authUser(@RequestBody request: AuthenticationRequest): AuthenticationResult {
       return userService.authUser(request)
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh authentication",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Authentication error", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun refreshToken(authentication: Authentication): AuthenticationResult =
        userService.refreshToken(authentication)

    private fun UserRequestDto.toModel() = UserModel(this.name, this.password, Status.OFFLINE)
}