package com.itmo.microservices.demo.bombardier

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.net.URL
import kotlin.properties.Delegates

@ConfigurationProperties(prefix = "bombardier", ignoreInvalidFields = false, ignoreUnknownFields = false)
@Component
class BombardierProperties {
    var teams by Delegates.notNull<List<Map<String, String>>>()
    var authEnabled by Delegates.notNull<Boolean>()
    fun getDescriptors() = teams.map { ServiceDescriptor(it["name"]!!, URL(it["url"]!!)) }
}

data class ServiceDescriptor(var name: String, var url: URL)