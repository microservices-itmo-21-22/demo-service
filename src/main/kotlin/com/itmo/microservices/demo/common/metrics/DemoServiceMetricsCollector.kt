package com.itmo.microservices.demo.common.metrics

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector
import io.micrometer.core.instrument.*
import org.springframework.beans.factory.annotation.Autowired
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class DemoServiceMetricsCollector(serviceName: String): CommonMetricsCollector(serviceName) {
    constructor() : this(SERVICE_NAME)

    lateinit var catalogShownCounter: Counter
    lateinit var itemAddedCounter: Counter
    lateinit var orderCreatedCounter: Counter
    lateinit var itemBookRequestSuccessCounter: Counter
    lateinit var itemBookRequestFailedCounter: Counter
    lateinit var finalizationAttemptSuccessCounter: Counter
    lateinit var finalizationAttemptFailedCounter: Counter
    lateinit var finalizationDurationSummary: Timer
    lateinit var currentShippingOrdersGauge: AtomicInteger
    lateinit var shippingOrdersTotalCounter: Counter
    lateinit var timeslotSetCounter: Counter

    @Autowired
    fun setMetrics(meterRegistry: MeterRegistry) {
        //Количество просмотров каталога продукции
        catalogShownCounter = meterRegistry.counter("catalog_shown")
        //Количество добавлений товара (товаров) в заказ
        itemAddedCounter = meterRegistry.counter("item_added")
        //Создание нового заказа
        orderCreatedCounter = meterRegistry.counter("order_created")
        //Количество запросов на бронирование товаров для заказа
        itemBookRequestSuccessCounter = meterRegistry.counter("item_book_request", listOf(Tag.of("result", "SUCCESS")))
        itemBookRequestFailedCounter = meterRegistry.counter("item_book_request", listOf(Tag.of("result", "FAILED")))
        //Количество запросов на финализацию заказа
        finalizationAttemptSuccessCounter = meterRegistry.counter("finalization_attempt", listOf(Tag.of("result", "SUCCESS")))
        finalizationAttemptFailedCounter = meterRegistry.counter("finalization_attempt", listOf(Tag.of("result", "FAILED")))
        //Длительность процесса финализации +0.9 квантиль???
        finalizationDurationSummary = meterRegistry.timer("finalization_duration")
        //Количество заказов, которые прямо сейчас находятся в доставке
        currentShippingOrdersGauge = meterRegistry.gauge("current_shipping_orders", AtomicInteger())!!
        //Количество заказов, переданных в доставку
        shippingOrdersTotalCounter = meterRegistry.counter("shipping_orders_total")
        //Время доставки выставлено (выбран таймслот) - количество запросов
        timeslotSetCounter = meterRegistry.counter("timeslot_set_request_count")
    }

    companion object {
        const val SERVICE_NAME = "demo_service"
    }
}
