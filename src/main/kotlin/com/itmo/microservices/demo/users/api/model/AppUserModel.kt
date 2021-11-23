package com.itmo.microservices.demo.users.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class AppUserModel(
        val id: UUID,
        val ipaddress: String,
        val name : String,
        val username: String,
        val email: String,
        val phone: String,
        val lastBasketId: UUID,
        @JsonIgnore
        val password: String) {
        fun userDetails(): UserDetails = User(username, password, emptyList())
}
