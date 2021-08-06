package com.itmo.microservices.demo.common.security

import org.springframework.http.HttpHeaders
import javax.servlet.http.HttpServletRequest

fun retrieveToken(request: HttpServletRequest): String? {
    if (request.getHeader(HttpHeaders.AUTHORIZATION) == null)
        return null
    val authHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION)!!
    if (!authHeaderValue.startsWith("Bearer ", ignoreCase = true))
        return null
    return authHeaderValue.substring("Bearer ".length)
}