package com.itmo.microservices.demo.common.metrics

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector
import io.micrometer.core.instrument.*
import org.springframework.beans.factory.annotation.Autowired
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.prometheus.client.Histogram
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
    // Не корректно работает
    lateinit var currentShippingOrdersGauge: AtomicInteger
    lateinit var shippingOrdersTotalCounter: Counter
    lateinit var timeslotSetCounter: Counter
    lateinit var addToFinilizedOrderRequestCounter: Counter
    lateinit var currentAbandonedOrderNumGauge: AtomicInteger
    lateinit var discardedOrdersCounter: Counter

    lateinit var fromCollectingToDiscardStatusCounter: Counter
    lateinit var fromDiscardToCollectingStatusCounter: Counter
    lateinit var fromCollectingToBookedStatusCounter: Counter
    lateinit var fromBookedToPaidStatusCounter: Counter
    lateinit var fromShippingToCompletedStatusCounter: Counter

    lateinit var ordersInStatusCollecting: AtomicInteger
    lateinit var ordersInStatusBooked: AtomicInteger
    lateinit var ordersInStatusCompleted: AtomicInteger
    lateinit var ordersInStatusDiscard: AtomicInteger
    lateinit var ordersInStatusPaid: AtomicInteger
    lateinit var ordersInStatusRefund: AtomicInteger
    lateinit var ordersInStatusShipping: AtomicInteger
    //lateinit var ordersInStatusHistogram: Histogram

    lateinit var averagedBookingToPayTime: Timer
    lateinit var revenueCounter: Counter
    lateinit var externalSystemExpensePaymentCounter: Counter
    lateinit var externalSystemExpenseDeliveryCounter: Counter
    lateinit var externalSystemExpenseNotificationCounter: Counter
    lateinit var refundedMoneyAmountDeliveryFailedCounter: Counter


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
        //Количество запросов на изменение заказа (добавление товара) после финализации!
        addToFinilizedOrderRequestCounter = meterRegistry.counter("add_to_finilized_order_request")
        //Количество “брошенных” (не финализированные) корзин - тех, которые были задетектированы и пока не были удалены или восстановлены
        currentAbandonedOrderNumGauge = meterRegistry.gauge("current_abandoned_order_num", AtomicInteger())!!

        // Количество “брошенных” корзин - тех, которые были удалены
        discardedOrdersCounter = meterRegistry.counter("discarded_orders")

        // Изменение статуса заказа
        fromCollectingToDiscardStatusCounter = meterRegistry.counter(
            "order_status_changed",
            listOf(Tag.of("fromState", "COLLECTING"), Tag.of("toState", "DISCARD"))
        )
        fromDiscardToCollectingStatusCounter = meterRegistry.counter(
            "order_status_changed",
            listOf(Tag.of("fromState", "DISCARD"), Tag.of("toState", "COLLECTING"))
        )
        fromCollectingToBookedStatusCounter = meterRegistry.counter(
            "order_status_changed",
            listOf(Tag.of("fromState", "COLLECTING"), Tag.of("toState", "BOOKED"))
        )
        fromBookedToPaidStatusCounter = meterRegistry.counter(
            "order_status_changed",
            listOf(Tag.of("fromState", "BOOKED"), Tag.of("toState", "PAID"))
        )
        fromShippingToCompletedStatusCounter = meterRegistry.counter(
            "order_status_changed",
            listOf(Tag.of("fromState", "SHIPPING"), Tag.of("toState", "COMPLETED"))
        )

        //Среднее время, которое проходит от бронирования до оплаты заказа. + 0.9 квантили
        averagedBookingToPayTime = meterRegistry.timer("avg_booking_to_payed_time")
        //Количество денег, которые были заработаны при успешных оплатах
        revenueCounter = meterRegistry.counter("revenue")
        //Количество денег, которые были потрачены при обращении во внешние системы
        externalSystemExpensePaymentCounter = meterRegistry.counter("external_system_expense", listOf(Tag.of("externalSystemType", "PAYMENT")))
        externalSystemExpenseDeliveryCounter = meterRegistry.counter("external_system_expense", listOf(Tag.of("externalSystemType", "DELIVERY")))
        externalSystemExpenseNotificationCounter = meterRegistry.counter("external_system_expense", listOf(Tag.of("externalSystemType", "NOTIFICATION")))

        //Количество денег, возвращенных пользователю
        refundedMoneyAmountDeliveryFailedCounter = meterRegistry.counter("refunded_money_amount", listOf(Tag.of("refundReason", "DELIVERY_FAILED")))

        ordersInStatusCollecting = meterRegistry.gauge("orders_in_status_collecting", AtomicInteger())!!
        ordersInStatusDiscard = meterRegistry.gauge("orders_in_status_discard", AtomicInteger())!!
        ordersInStatusBooked = meterRegistry.gauge("orders_in_status_booked", AtomicInteger())!!
        ordersInStatusPaid = meterRegistry.gauge("orders_in_status_paid", AtomicInteger())!!
        ordersInStatusShipping = meterRegistry.gauge("orders_in_status_shipping", AtomicInteger())!!
        ordersInStatusRefund = meterRegistry.gauge("orders_in_status_refund", AtomicInteger())!!
        ordersInStatusCompleted = meterRegistry.gauge("orders_in_status_completed", AtomicInteger())!!
    }

    companion object {
        const val SERVICE_NAME = "demo_service"
    }
}
