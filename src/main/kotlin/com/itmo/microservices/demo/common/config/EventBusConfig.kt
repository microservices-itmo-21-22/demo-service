package com.itmo.microservices.demo.common.config

import com.google.common.eventbus.AsyncEventBus
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class EventBusConfig {

    @Bean(name = ["eventBusExecutor"])
    fun eventBusExecutor(): ExecutorService = Executors.newFixedThreadPool(5)

    @Bean
    @Suppress("UnstableApiUsage")
    fun eventBus(@Qualifier("eventBusExecutor") executor: Executor) = AsyncEventBus(executor)
}