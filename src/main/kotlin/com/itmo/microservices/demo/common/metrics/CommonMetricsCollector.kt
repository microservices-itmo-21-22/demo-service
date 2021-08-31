package com.itmo.microservices.demo.common.metrics

import com.itmo.microservices.demo.common.logging.NotableEvent
import io.micrometer.core.instrument.Metrics
import io.prometheus.client.Counter

open class CommonMetricsCollector(private val serviceName: String) {

    private val prometheusEventsCounter = createAndRegisterCounter(
        EVENTS,
        "Amount of occurred events",
        EVENTS_TYPE_LABEL
    )

    fun countEvent(event: NotableEvent) {
        Metrics.counter(EVENTS, EVENTS_TYPE_LABEL, event.getName()).increment()
        prometheusEventsCounter.labels(event.getName()).inc()
    }

    private fun createAndRegisterCounter(name: String, help: String, vararg labels: String): Counter {
        return Counter.build()
            .namespace(serviceName)
            .name(name)
            .help(help)
            .labelNames(*labels)
            .register()
    }

    companion object {
        const val EVENTS = "events_total"
        const val EVENTS_TYPE_LABEL = "type"
    }
}