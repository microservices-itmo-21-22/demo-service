package com.itmo.microservices.demo.users.impl.util

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.impl.entity.AppUser

fun AppUser.toModel(): AppUserModel = kotlin.runCatching {
    AppUserModel(
        username = this.username!!,
        name = this.name!!,
        surname = this.surname!!,
        email = this.email!!,
        password = this.password!!
    )
}.getOrElse { exception -> throw IllegalStateException("Some of user fields are null", exception) }