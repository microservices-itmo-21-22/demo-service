package com.itmo.microservices.demo.common.metrics

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector
import io.micrometer.core.instrument.*
import org.springframework.beans.factory.annotation.Autowired
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class DemoServiceMetricsCollector(serviceName: String): CommonMetricsCollector(serviceName) {
    constructor() : this(SERVICE_NAME)

    lateinit var catalogShownCounter: Counter
    lateinit var itemAddedCounter: Counter
    lateinit var orderCreatedCounter: Counter
    lateinit var shippingOrdersCounter: Counter
    lateinit var timeslotSetCounter: Counter
    lateinit var catalogShown: Counter
    lateinit var itemAdded: Counter
    lateinit var orderCreated: Counter
    lateinit var itemBookRequestSuccess: Counter
    lateinit var itemBookRequestFailed: Counter
    lateinit var finalizationAttemptSuccess: Counter
    lateinit var finalizationAttemptFailed: Counter
    lateinit var finalizationDuration: Timer
    lateinit var currentShippingOrders: AtomicInteger
    lateinit var shippingOrdersTotal: Counter

    @Autowired
    fun setMetrics(meterRegistry: MeterRegistry) {
        catalogShownCounter = meterRegistry.counter("catalog_shown")
        itemAddedCounter = meterRegistry.counter("item_added")
        orderCreatedCounter = meterRegistry.counter("order_created")
        shippingOrdersCounter = meterRegistry.counter("shipping_orders_total")
        timeslotSetCounter = meterRegistry.counter("timeslot_set_request_count")
        //Количество просмотров каталога продукции
        catalogShown = meterRegistry.counter("catalog.shown", listOf(Tag.of("serviceName", "p07")))
        //Количество добавлений товара (товаров) в заказ
        itemAdded = meterRegistry.counter("item.added", listOf(Tag.of("serviceName", "p07")))
        //Создание нового заказа
        orderCreated = meterRegistry.counter("order.created", listOf(Tag.of("serviceName", "p07")))
        //Количество запросов на бронирование товаров для заказа
        itemBookRequestSuccess = meterRegistry.counter("item.book.request", listOf(Tag.of("serviceName", "p07"), Tag.of("result", "SUCCESS")))
        itemBookRequestFailed = meterRegistry.counter("item.book.request", listOf(Tag.of("serviceName", "p07"), Tag.of("result", "FAILED")))
        //Количество запросов на финализацию заказа
        finalizationAttemptSuccess = meterRegistry.counter("finalization.attempt", listOf(Tag.of("serviceName", "p07"), Tag.of("result", "SUCCESS")))
        finalizationAttemptFailed = meterRegistry.counter("finalization.attempt", listOf(Tag.of("serviceName", "p07"), Tag.of("result", "FAILED")))
        //Длительность процесса финализации +0.9 квантиль???
        finalizationDuration = meterRegistry.timer("finalization.duration", listOf(Tag.of("serviceName", "p07")))
        currentShippingOrders = meterRegistry.gauge("current.shipping.orders", listOf(Tag.of("serviceName", "p07")), AtomicInteger())!!
        //Количество заказов, переданных в доставку
        shippingOrdersTotal = meterRegistry.counter("shipping.orders.total")
    }

    companion object {
        const val SERVICE_NAME = "demo_service"
    }
}
