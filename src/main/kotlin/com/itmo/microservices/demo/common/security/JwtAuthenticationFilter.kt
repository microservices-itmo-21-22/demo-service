package com.itmo.microservices.demo.common.security

import com.itmo.microservices.demo.auth.impl.service.JwtTokenManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter(private val tokenManager: JwtTokenManager): OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse,
                                  filterChain: FilterChain) {
        val token = retrieveToken(request)
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }
        kotlin.runCatching { tokenManager.readAccessToken(token) }
                .onSuccess { user -> SecurityContextHolder.getContext().authentication =
                        UsernamePasswordAuthenticationToken(user, token, user.authorities) }
        filterChain.doFilter(request, response)
    }
}