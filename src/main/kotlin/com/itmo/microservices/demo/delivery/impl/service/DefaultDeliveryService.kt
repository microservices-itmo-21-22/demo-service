package com.itmo.microservices.demo.delivery.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.delivery.impl.repository.DeliveryRepository
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PostConstruct
import kotlin.concurrent.*


@Suppress("UnstableApiUsage")
@Service
class DefaultDeliveryService(private val deliveryRepository: DeliveryRepository,
                             private val eventBus: EventBus
): DeliveryService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    var countOrdersWaitingForDeliver = AtomicInteger(0)
    //Virtual time
    var time:Int=0
    @PostConstruct
    fun timerStart(){
        val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        executor.scheduleAtFixedRate(
            {this.time++},
            0,
            1000,
            TimeUnit.MILLISECONDS);

    }

    override fun getSlots(number: Int): List<Int> {
        var list = mutableListOf<Int>()
        var startTime:Int = time+500+3*countOrdersWaitingForDeliver.get()
        for (i:Int in 1..number){
            list.add(startTime)
            startTime+=3
        }
        return list
    }

    override fun getDeliveryHistoryById(id: String) {
        TODO("Not yet implemented")
    }

}
