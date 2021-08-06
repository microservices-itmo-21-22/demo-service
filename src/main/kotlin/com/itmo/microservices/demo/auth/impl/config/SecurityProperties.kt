package com.itmo.microservices.demo.auth.impl.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

@ConfigurationProperties("security")
@Component
class SecurityProperties {
    var secret: String = "sec12345678"
    var tokenLifetime: Duration = Duration.ofMinutes(15)
    var refreshTokenLifetime: Duration = Duration.ofDays(30)
}