package com.itmo.microservices.demo.users.impl.util

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.impl.entity.AppUser

fun AppUser.toModel(): AppUserModel = kotlin.runCatching {
    AppUserModel(
        id = this.id!!,
        ipaddress = this.ipaddress,
        username = this.username,
        name = this.name!!,
        email = this.email,
        password = this.password!!,
        phone = this.phone,
        lastBasketId = this.lastBasketId!!
    )
}.getOrElse { exception -> throw IllegalStateException("Some of user fields are null", exception) }