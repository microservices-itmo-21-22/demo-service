package com.itmo.microservices.demo.common.security

import org.springframework.http.HttpHeaders
import javax.servlet.http.HttpServletRequest

fun retrieveToken(request: HttpServletRequest): String? {
    val authHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
    if (!authHeaderValue.startsWith("Bearer ", ignoreCase = true))
        return null
    return authHeaderValue.substring("Bearer ".length)
}