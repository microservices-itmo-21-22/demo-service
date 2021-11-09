package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.demo.bombardier.external.BookingStatus
import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.external.OrderStatus
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi

class OrderFinalizingStage(private val externalServiceApi: ExternalServiceApi) : TestStage {
    companion object {
        val log = CoroutineLoggingFactory.getLogger(OrderFinalizingStage::class.java)
    }

    override suspend fun run(): TestStage.TestContinuationType {
        log.info("Starting booking items stage for order ${testCtx().orderId}")
        val orderStateBeforeFinalizing = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!)

        val bookingResult = externalServiceApi.bookOrder(testCtx().userId!!, testCtx().orderId!!)

        val orderStateAfterBooking = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!)

        val bookingRecords = externalServiceApi.getBookingHistory(bookingResult.id)
        for (item in orderStateAfterBooking.itemsMap.keys) {
            if (bookingRecords.none { it.itemId == item.id }) {
                log.error("Cannot find booking log record: booking id = ${bookingResult.id}; itemId = ${item.id}; orderId = ${testCtx().orderId}")
                return TestStage.TestContinuationType.FAIL
            }
        }

        when (orderStateAfterBooking.status) { //TODO Elina рассмотреть результат discard
            OrderStatus.OrderBooked -> {
                if (bookingResult.failedItems.isNotEmpty()) {
                    log.error("Order ${testCtx().orderId} is booked, but there are failed items")
                    return TestStage.TestContinuationType.FAIL
                }

                for (item in orderStateAfterBooking.itemsMap.keys) {
                    val itemRecord = bookingRecords.firstOrNull { it.itemId == item.id }
                    if (itemRecord == null || itemRecord.status != BookingStatus.SUCCESS) {
                        log.error(
                            "Booking ${bookingResult.id} of order ${testCtx().orderId} is marked as successful, " +
                                    "but item ${item.id} is marked as ${itemRecord?.status}"
                        )
                        return TestStage.TestContinuationType.FAIL
                    }
                }
                log.info("Successfully validated all items in BOOKED order ${testCtx().orderId}")
            }
            OrderStatus.OrderCollecting -> {
                if (bookingResult.failedItems.isEmpty()) {
                    log.error("Booking of order ${testCtx().orderId} failed, but booking ${bookingResult.id} doesn't have failed items")
                    return TestStage.TestContinuationType.FAIL
                }

                val failed = bookingRecords
                    .filter { it.status != BookingStatus.SUCCESS }
                    .map { it.itemId }
                    .toSet()

                if (failed != bookingResult.failedItems) {
                    log.error("List of failed items ${bookingResult.failedItems} doesn't match failed booking info of items $failed")
                    return TestStage.TestContinuationType.FAIL
                }

                val failedList = orderStateAfterBooking.itemsMap.filter { it.key.id in failed }
                    .map { Triple(it.key.id, it.key.title, it.value) }

                log.info("Successfully validated all items in NOT BOOKED order ${testCtx().orderId}, failed items: $failedList")
                return TestStage.TestContinuationType.STOP
            }
            else -> {
                log.error(
                    "Illegal transition for order ${orderStateAfterBooking.id} from ${orderStateBeforeFinalizing.status} " +
                            "to ${orderStateAfterBooking.status}"
                )
                return TestStage.TestContinuationType.FAIL
            }
        }

        return TestStage.TestContinuationType.CONTINUE
    }
}