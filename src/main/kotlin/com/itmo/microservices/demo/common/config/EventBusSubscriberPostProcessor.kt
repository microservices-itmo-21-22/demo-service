package com.itmo.microservices.demo.common.config

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Configuration
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

@Configuration
@Suppress("UnstableApiUsage")
class EventBusSubscriberPostProcessor(private val eventBus: EventBus): BeanPostProcessor {

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        runCatching {
            if (bean::class.memberFunctions.any { func -> func.hasAnnotation<Subscribe>() })
                eventBus.register(bean)
        }
        return bean
    }
}