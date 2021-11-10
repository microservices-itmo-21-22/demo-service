package com.itmo.microservices.demo.common.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
class SecurityConfiguration(
        private val authenticationFilter: JwtAuthenticationFilter,
        private val refreshAuthenticationFilter: RefreshJwtAuthenticationFilter
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    override fun configure(http: HttpSecurity) {
        http
                .cors().configurationSource {
                    CorsConfiguration()
                            .also { it.allowedOrigins = listOf("*") }
                            .also { it.allowedHeaders = listOf("*") }
                            .also { it.allowedMethods = listOf("*") }
                }.and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.POST, "/authentication").permitAll()
                .antMatchers(HttpMethod.POST, "/authentication/refresh").hasAuthority("REFRESH")
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().hasAuthority("ACCESS")
                .and()
                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
                .addFilterAfter(refreshAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
                .headers().frameOptions().sameOrigin()
    }
}
