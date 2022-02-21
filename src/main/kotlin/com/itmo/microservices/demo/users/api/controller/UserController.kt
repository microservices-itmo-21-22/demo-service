package com.itmo.microservices.demo.users.api.controller

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import com.itmo.microservices.demo.users.api.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping
    @Operation(
        summary = "Register new user",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Not acceptable:User already exists", responseCode = "406", content = [Content()])
        ]
    )
    fun register(@RequestBody request: RegistrationRequest) = userService.registerUser(request)

    @GetMapping("/{user_id}")
    @Operation(
        summary = "Get current user info",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getAccountData(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails?, @PathVariable user_id: UUID): AppUserModel =
            userService.getAccountData(user, user_id)
}