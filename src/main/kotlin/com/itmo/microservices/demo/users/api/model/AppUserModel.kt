package com.itmo.microservices.demo.users.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class AppUserModel(
        var id: UUID,
        val name: String,
        @JsonIgnore
        val password: String) {

        fun userDetails(): UserDetails = User(name, password, emptyList())
}
