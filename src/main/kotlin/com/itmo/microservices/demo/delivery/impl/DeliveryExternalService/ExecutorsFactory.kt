package com.itmo.microservices.demo.delivery.DeliveryExternalService

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ExecutorsFactory {
    companion object {

        @JvmStatic
        fun executor(
            threadNum: Int = Runtime.getRuntime().availableProcessors(),
            maxQueueSize: Int = 1000
        ) = ThreadPoolExecutor(
            threadNum,
            threadNum,
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(maxQueueSize)
        )
    }
}