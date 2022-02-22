package com.itmo.microservices.demo.common.metrics

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector
import io.prometheus.client.Counter
import org.springframework.stereotype.Component

@Component
class DemoServiceMetricsCollector(serviceName: String): CommonMetricsCollector(serviceName) {
    constructor() : this(SERVICE_NAME)

    val counter: Counter =
        Counter.build().name("catalog_shown")
            .help("Количество просмотров каталога продукции")
            .create()
            .register()

    companion object {
        const val SERVICE_NAME = "demo_service"
    }
}
