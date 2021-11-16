package com.itmo.microservices.demo.bombardier.stages

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.bombardier.external.BookingStatus
import com.itmo.microservices.demo.bombardier.flow.CoroutineLoggingFactory
import com.itmo.microservices.demo.bombardier.external.OrderStatus
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import com.itmo.microservices.demo.bombardier.logging.OrderCommonNotableEvents
import com.itmo.microservices.demo.bombardier.logging.OrderFinaizingNotableEvents.*
import org.springframework.stereotype.Component

@Component
class OrderFinalizingStage : TestStage {
    @InjectEventLogger
    private lateinit var eventLogger: EventLogger


    override suspend fun run(userManagement: UserManagement, externalServiceApi: ExternalServiceApi): TestStage.TestContinuationType {
        eventLogger.info(I_START_FINALIZING, testCtx().orderId)
        val orderStateBeforeFinalizing = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!)

        val bookingResult = externalServiceApi.bookOrder(testCtx().userId!!, testCtx().orderId!!)

        val orderStateAfterBooking = externalServiceApi.getOrder(testCtx().userId!!, testCtx().orderId!!)

        val bookingRecords = externalServiceApi.getBookingHistory(bookingResult.id)
        for (item in orderStateAfterBooking.itemsMap.keys) {
            if (bookingRecords.none { it.itemId == item.id }) {
                eventLogger.error(E_BOOKING_LOG_RECORD_NOT_FOUND, bookingResult.id, item.id, testCtx().orderId)
                return TestStage.TestContinuationType.FAIL
            }
        }

        when (orderStateAfterBooking.status) { //TODO Elina рассмотреть результат discard
            OrderStatus.OrderBooked -> {
                if (bookingResult.failedItems.isNotEmpty()) {
                    eventLogger.error(E_ORDER_HAS_FAIL_ITEMS, testCtx().orderId)
                    return TestStage.TestContinuationType.FAIL
                }

                for (item in orderStateAfterBooking.itemsMap.keys) {
                    val itemRecord = bookingRecords.firstOrNull { it.itemId == item.id }
                    if (itemRecord == null || itemRecord.status != BookingStatus.SUCCESS) {
                        eventLogger.error(
                            E_ITEMS_FAIL,
                            bookingResult.id,
                            testCtx().orderId,
                            item.id,
                            itemRecord?.status
                        )
                        return TestStage.TestContinuationType.FAIL
                    }
                }
                eventLogger.info(I_SUCCESS_VALIDATE_BOOKED, testCtx().orderId)
            }
            OrderStatus.OrderCollecting -> {
                if (bookingResult.failedItems.isEmpty()) {
                    eventLogger.error(E_BOOKING_FAIL_BUT_ITEMS_SUCCESS, testCtx().orderId, bookingResult.id)
                    return TestStage.TestContinuationType.FAIL
                }

                val failed = bookingRecords
                    .filter { it.status != BookingStatus.SUCCESS }
                    .map { it.itemId }
                    .toSet()

                if (failed != bookingResult.failedItems) {
                    eventLogger.error(E_LIST_FAILED_ITEMS_MISMATCH, bookingResult.failedItems, failed)
                    return TestStage.TestContinuationType.FAIL
                }

                val failedList = orderStateAfterBooking.itemsMap.filter { it.key.id in failed }
                    .map { Triple(it.key.id, it.key.title, it.value) }

                eventLogger.info(I_SUCCESS_VALIDATE_NOT_BOOKED, testCtx().orderId, failedList)
                return TestStage.TestContinuationType.STOP
            }
            else -> {
                eventLogger.error(
                    OrderCommonNotableEvents.E_ILLEGAL_ORDER_TRANSITION,
                    orderStateAfterBooking.id,
                    orderStateBeforeFinalizing.status,
                    orderStateAfterBooking.status
                )
                return TestStage.TestContinuationType.FAIL
            }
        }

        return TestStage.TestContinuationType.CONTINUE
    }
}