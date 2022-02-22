package com.itmo.microservices.demo.common.metrics

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DemoServiceMetricsCollector(serviceName: String): CommonMetricsCollector(serviceName) {
    constructor() : this(SERVICE_NAME)

    lateinit var catalogShownCounter: Counter
    lateinit var itemAddedCounter: Counter
    lateinit var orderCreatedCounter: Counter
    lateinit var shippingOrdersCounter: Counter
    lateinit var timeslotSetCounter: Counter

    @Autowired
    fun setMetrics(meterRegistry: MeterRegistry) {
        catalogShownCounter = meterRegistry.counter("catalog_shown")
        itemAddedCounter = meterRegistry.counter("item_added")
        orderCreatedCounter = meterRegistry.counter("order_created")
        shippingOrdersCounter = meterRegistry.counter("shipping_orders_total")
        timeslotSetCounter = meterRegistry.counter("timeslot_set_request_count")
    }

    companion object {
        const val SERVICE_NAME = "demo_service"
    }
}
