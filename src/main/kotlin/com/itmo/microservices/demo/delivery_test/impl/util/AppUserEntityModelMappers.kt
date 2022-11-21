package com.itmo.microservices.demo.users.impl.util

import com.itmo.microservices.demo.users.api.model.UserDTO
import com.itmo.microservices.demo.users.impl.entity.User

fun User.toModel(): UserDTO = kotlin.runCatching {
    UserDTO(
        id = this.id!!,
        name = this.name!!,
        password = this.password!!
    )
}.getOrElse { exception -> throw IllegalStateException("Some of user fields are null", exception) }

fun UserDTO.toEntity(): User = kotlin.runCatching {
    User(
        id = this.id,
        name = this.name,
        password = this.password
    )
}.getOrElse { exception -> throw IllegalStateException("Some of user fields are null", exception) }