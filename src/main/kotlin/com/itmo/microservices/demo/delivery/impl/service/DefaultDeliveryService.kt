package com.itmo.microservices.demo.delivery.impl.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.metrics.DemoServiceMetricsCollector
import com.itmo.microservices.demo.delivery.api.model.DeliveryInfoRecordModel
import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.delivery.impl.entity.DeliveryInfoRecord
import com.itmo.microservices.demo.delivery.impl.entity.DeliverySubmissionOutcome
import com.itmo.microservices.demo.delivery.impl.repository.DeliveryInfoRecordRepository
import com.itmo.microservices.demo.delivery.impl.utils.toModel
import com.itmo.microservices.demo.notifications.impl.service.StubNotificationService
import com.itmo.microservices.demo.order.api.model.OrderDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PostConstruct
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaDuration


@Service
class Timer {
    //Virtual time
    var time: Int = 0



    @PostConstruct
    fun timerStart() {
        val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        executor.scheduleAtFixedRate(
            { this.time++ },
            0,
            1000,
            TimeUnit.MILLISECONDS
        )
    }

    fun get_time() = this.time
}


@Suppress("UnstableApiUsage")
@Service
class DefaultDeliveryService(

    private val deliveryInfoRecordRepository: DeliveryInfoRecordRepository,
    private val eventBus: EventBus,
    private val timer: Timer
) : DeliveryService {

    @Autowired
    private lateinit var metricsCollector: DemoServiceMetricsCollector

    private val postToken = mapOf("clientSecret" to "8ddfb4e8-7f83-4c33-b7ac-8504f7c99205")
    private val objectMapper = ObjectMapper()
    private val postBody: String = objectMapper.writeValueAsString(postToken)

    @OptIn(ExperimentalTime::class)
    private val timeout = Duration.seconds(10).toJavaDuration()
    val httpClient: HttpClient = HttpClient.newBuilder().build()

    private fun getPostHeaders(body: String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create("http://77.234.215.138:30027/transactions"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
    }


    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    companion object {
        val log: Logger = LoggerFactory.getLogger(StubNotificationService::class.java)
    }

    @Autowired
    var pollingForResult: PollingForResult? = null

    var countOrdersWaitingForDeliver = AtomicInteger(0)



    override fun getSlots(number: Int): List<Int> {
        var list = mutableListOf<Int>()
        var startTime: Int = timer.get_time() + 30 + 3 * countOrdersWaitingForDeliver.get()
        for (i: Int in 1..number) {
            list.add(startTime)
            startTime += 3
        }
        log.info("return list of slot")
        return list.toList()
    }

    override fun getDeliveryHistoryById(transactionId: String):List<DeliveryInfoRecordModel> {
       val list =  deliveryInfoRecordRepository.getAllByTransactionId(UUID.fromString(transactionId))
        var mList = mutableListOf<DeliveryInfoRecordModel>()
        for(e in list){
            mList.add(e.toModel())
        }
        return mList.toList()
    }


    override fun delivery(order: OrderDto) {
        delivery(order, 1)
    }

    override fun delivery(order: OrderDto, times: Int) {
        if (order.deliveryDuration!! < this.timer.get_time()) {
            metricsCollector.shippingOrdersTotal.increment()
            log.info("order.deliveryDuration "+order.deliveryDuration)
            log.info("this.timer.get_time() "+this.timer.get_time())
            log.info("a delivery EXPIRED")
            val timeStamp = System.currentTimeMillis()
            deliveryInfoRecordRepository.save(
                DeliveryInfoRecord(
                    DeliverySubmissionOutcome.EXPIRED,
                    timeStamp,
                    1,
                    timeStamp,
                    order.id!!,
                    timeStamp
                )
            )
        } else {
            try {
                log.info("send delivery requesting")
                val response = httpClient.send(getPostHeaders(postBody), HttpResponse.BodyHandlers.ofString())
                val responseJson = JSONObject(response.body())
                if (response.statusCode() == 200) {
                    val id = responseJson.getString("id")
                    log.info("delivery processing , maybe fail")
                    pollingForResult?.getDeliveryResult(order, responseJson, 1)

                } else {
                    Thread.sleep(3000)
                    delivery(order)
                }
            } catch (e: HttpConnectTimeoutException) {
                log.info("Request timeout!")
                delivery(order)
            }
        }
    }
}


@Service
class PollingForResult(
    private val deliveryInfoRecordRepository: DeliveryInfoRecordRepository,
    private val timer: Timer
) {
    private val postToken = mapOf("clientSecret" to "8ddfb4e8-7f83-4c33-b7ac-8504f7c99205")
    private val objectMapper = ObjectMapper()
    private val postBody: String = objectMapper.writeValueAsString(postToken)

    @OptIn(ExperimentalTime::class)
    private val timeout = Duration.seconds(10).toJavaDuration()
    val httpClient: HttpClient = HttpClient.newBuilder().build()

    private fun getGetHeaders(id: String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create("http://77.234.215.138:30027/transactions/$id"))
            .timeout(this.timeout)
            .header("Content-Type", "application/json")
            .build()
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(StubNotificationService::class.java)
    }

    var schedulePool: ScheduledExecutorService = Executors.newScheduledThreadPool(2)

    fun getDeliveryResult(order: OrderDto, responseJson_post: JSONObject, times: Int) {
        if (times > 3) {
            return
        }
        schedulePool.schedule(
            {
                log.info("getting status from external system")
                val response_poll = httpClient.send(
                    getGetHeaders(responseJson_post.getString("id")),
                    HttpResponse.BodyHandlers.ofString()
                )
                val responseJson_poll = JSONObject(response_poll.body())
                log.info("getting response from 3th system")
                if (responseJson_poll.getString("status") == "SUCCESS") {
                    log.info("delivery success")
                    deliveryInfoRecordRepository.save(
                        DeliveryInfoRecord(
                            DeliverySubmissionOutcome.SUCCESS,
                            responseJson_poll.getLong("delta"),
                            times,
                            responseJson_poll.getLong("completedTime"),
                            order.id!!,
                            responseJson_poll.getLong("submitTime")
                        )
                    )
                } else {
                    log.info("delivery fail")
                    deliveryInfoRecordRepository.save(
                        DeliveryInfoRecord(
                            DeliverySubmissionOutcome.FAILURE,
                            responseJson_poll.getLong("delta"),
                            times,
                            responseJson_poll.getLong("completedTime"),
                            order.id!!,
                            responseJson_poll.getLong("submitTime")
                        )
                    )
                }

            }, (10).toLong(), TimeUnit.SECONDS
        )
    }

}




