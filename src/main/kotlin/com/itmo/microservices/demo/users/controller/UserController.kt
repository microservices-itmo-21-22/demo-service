package com.itmo.microservices.demo.users.controller

import com.itmo.microservices.demo.users.entity.AppUser
import com.itmo.microservices.demo.users.model.AppUserDto
import com.itmo.microservices.demo.users.model.RegistrationRequest
import com.itmo.microservices.demo.users.service.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun register(@RequestBody request: RegistrationRequest) {
    }

    @GetMapping("/me")
    fun getCurrentUserInfo(@AuthenticationPrincipal user: AppUser): AppUserDto? = null

    @DeleteMapping("/me")
    fun deleteCurrentUser(@AuthenticationPrincipal user: AppUser) = Unit
}