package com.itmo.microservices.demo.common.logging

import net.logstash.logback.marker.Markers.append
import org.slf4j.Logger

const val testServiceFiledName = "test_service"

class LoggerWrapper(var logger:Logger, var serviceName: String) {
    fun info(msg: String){
        logger.info(append(testServiceFiledName, serviceName), msg)
    }

    fun error(msg: String){
        logger.error(append(testServiceFiledName, serviceName), msg)
    }

    fun error(msg: String, throwable: Throwable){
        logger.error(append(testServiceFiledName, serviceName), msg, throwable)
    }
}