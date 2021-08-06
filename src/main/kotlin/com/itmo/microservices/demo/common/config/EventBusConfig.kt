package com.itmo.microservices.demo.common.config

import com.google.common.eventbus.EventBus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventBusConfig {

    @Bean
    @Suppress("UnstableApiUsage")
    fun eventBus() = EventBus()
}