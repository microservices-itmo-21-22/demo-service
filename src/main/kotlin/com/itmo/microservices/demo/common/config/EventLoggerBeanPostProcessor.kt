package com.itmo.microservices.demo.common.config

import com.itmo.microservices.demo.common.annotations.InjectEventLogger
import com.itmo.microservices.demo.common.logging.EventLogger
import com.itmo.microservices.demo.common.metrics.CommonMetricsCollector
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Component
import org.springframework.util.ReflectionUtils

/**
 * Post processing injection of EventLogger
 * in class' fields marked by `InjectEventLogger` annotation
 */
@Component
class EventLoggerBeanPostProcessor(private val metrics: CommonMetricsCollector) : BeanPostProcessor {

    @Throws(BeansException::class)
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        process(bean)

        // We must return bean after completion of processing
        return bean
    }

    private fun process(bean: Any) {
        val beanClass: Class<*> = bean.javaClass
        val fields = beanClass.declaredFields

        // Search every field for annotation presence
        for (field in fields) {
            AnnotationUtils.getAnnotation(field, InjectEventLogger::class.java)
                ?: continue

            // Skip irrelevant fields

            // Allow access to private fields
            field.isAccessible = true
            val logger = LoggerFactory.getLogger(beanClass)

            // Inject our logger
            ReflectionUtils.setField(field, bean, EventLogger(logger, metrics))
        }
    }
}