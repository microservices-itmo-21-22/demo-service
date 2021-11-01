package com.itmo.microservices.demo.users.api.service

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import org.springframework.security.core.userdetails.UserDetails

// refactoring this to analytic standart.
@Deprecated("Need refactoring")
interface UserService {
    fun findUser(username: String): AppUserModel?
    fun registerUser(request: RegistrationRequest)
    fun getAccountData(requester: UserDetails): AppUserModel
    fun deleteUser(user: UserDetails)
}