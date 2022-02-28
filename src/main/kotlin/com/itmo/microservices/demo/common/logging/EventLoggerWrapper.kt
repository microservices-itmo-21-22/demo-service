package com.itmo.microservices.demo.common.logging

import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.commonlib.logging.NotableEvent
import net.logstash.logback.marker.Markers.append

class EventLoggerWrapper(var eventLogger: EventLogger, var serviceName: String) {

    fun info(event: NotableEvent, vararg payload: Any?) {
        eventLogger.info(append(testServiceFiledName, serviceName), event, *payload)
    }

    fun error(event: NotableEvent, vararg payload: Any?) {
        eventLogger.error(append(testServiceFiledName, serviceName), event, *payload)
    }
}