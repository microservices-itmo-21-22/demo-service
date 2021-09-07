package com.itmo.microservices.demo.common.metrics

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector
import org.springframework.stereotype.Component

@Component
class DemoServiceMetricsCollector(serviceName: String): CommonMetricsCollector(serviceName) {
    constructor() : this(SERVICE_NAME)

    companion object {
        const val SERVICE_NAME = "demo_service"
    }
}
