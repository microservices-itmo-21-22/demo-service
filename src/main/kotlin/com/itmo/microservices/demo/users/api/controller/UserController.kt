package com.itmo.microservices.demo.users.api.controller

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import com.itmo.microservices.demo.users.api.service.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun register(@RequestBody request: RegistrationRequest) = userService.registerUser(request)

    @GetMapping("/me")
    fun getAccountData(@AuthenticationPrincipal user: UserDetails): AppUserModel =
            userService.getAccountData(user)

    @DeleteMapping("/me")
    fun deleteCurrentUser(@AuthenticationPrincipal user: UserDetails) =
            userService.deleteUser(user)
}