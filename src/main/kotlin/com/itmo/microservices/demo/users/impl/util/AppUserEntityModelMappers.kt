package com.itmo.microservices.demo.users.impl.util

import com.itmo.microservices.demo.users.api.model.UserModel
import com.itmo.microservices.demo.users.impl.entity.User

fun User.toModel(): UserModel = kotlin.runCatching {
    UserModel(
        name = this.name!!,
        password = this.password!!,
        status = this.status!!
    )
}.getOrElse { exception -> throw IllegalStateException("Some of user fields are null", exception) }