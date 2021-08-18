package com.itmo.microservices.demo.users.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

data class AppUserModel(
        val username: String,
        val name: String,
        val surname: String,
        val email: String,
        @JsonIgnore
        val password: String) {

        fun userDetails(): UserDetails = User(username, password, emptyList())
}
