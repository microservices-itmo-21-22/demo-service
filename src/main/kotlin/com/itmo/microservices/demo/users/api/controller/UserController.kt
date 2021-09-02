package com.itmo.microservices.demo.users.api.controller

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import com.itmo.microservices.demo.users.api.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping
    @Operation(summary = "Register new user")
    fun register(@RequestBody request: RegistrationRequest) = userService.registerUser(request)

    @GetMapping("/me")
    @Operation(summary = "Get current user info", security = [SecurityRequirement(name = "bearerAuth")])
    fun getAccountData(@AuthenticationPrincipal user: UserDetails): AppUserModel =
            userService.getAccountData(user)

    @DeleteMapping("/me")
    @Operation(summary = "Delete current user", security = [SecurityRequirement(name = "bearerAuth")])
    fun deleteCurrentUser(@AuthenticationPrincipal user: UserDetails) =
            userService.deleteUser(user)
}