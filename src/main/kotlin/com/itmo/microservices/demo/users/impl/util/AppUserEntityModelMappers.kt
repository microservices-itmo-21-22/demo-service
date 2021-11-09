package com.itmo.microservices.demo.users.impl.util

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.impl.entity.AppUser

fun AppUser.toModel(): AppUserModel = kotlin.runCatching {
    AppUserModel(
        name = this.name!!,
        password = this.password!!
    )
}.getOrElse { exception -> throw IllegalStateException("Some of user fields are null", exception) }