package com.itmo.microservices.demo.payments.impl.service

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.payments.api.model.TransactionDto
import com.itmo.microservices.demo.payments.api.model.TransactionStatus
import com.itmo.microservices.demo.payments.impl.entity.Transaction
import com.itmo.microservices.demo.payments.impl.exception.ExternalServiceException
import com.itmo.microservices.demo.payments.impl.exception.TooManyParallelRequestException
import com.itmo.microservices.demo.payments.impl.logging.PaymentServiceNotableEvents
import com.itmo.microservices.demo.payments.impl.repository.TransactionRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration
import java.util.*

@Service
class TransactionRequestService(private val transactionRepository: TransactionRepository) {
    //    p07-transaction: 8ddfb4e8-7f83-4c33-b7ac-8504f7c99205
    //    p07-polling: 84ee19e4-f33f-4f60-80cb-765353745d10

    @InjectEventLogger
    private val eventLogger: EventLogger? = null

    private val CLIENT_SECRET = "84ee19e4-f33f-4f60-80cb-765353745d10" // p-07 polling
    private val URL = "http://77.234.215.138:30027/transactions/"

    private val ATTEMPTS_LIMIT = 5
    private val webClient: WebClient

    @Throws(InterruptedException::class)
    fun poll(transactionId: UUID): TransactionDto? {
        println(
            "Poll method. "
                    + Thread.currentThread().name
        )
        var result: TransactionDto?
        var delay = 100
        while (true) {
            Thread.sleep(delay.toLong())
            result = requestTransactionResponse(transactionId)
            val status = result!!.status
            if (status !== TransactionStatus.PENDING) {
                if (status === TransactionStatus.SUCCESS) {
                    eventLogger!!.info(PaymentServiceNotableEvents.I_EXTERNAL_SYSTEM_SUCCESS, result)
                } else {
                    eventLogger!!.info(PaymentServiceNotableEvents.I_EXTERNAL_SYSTEM_FAILURE, result)
                }
                val transaction = Transaction(
                    result.id,
                    result.status,
                    result.submitTime,
                    result.completedTime,
                    result.cost,
                    result.delta
                )
                println(transaction.status)
                transactionRepository.save(transaction)
                return result
            }
            delay *= 2
        }
    }

    fun requestTransactionResponse(transactionId: UUID): TransactionDto? {
        return webClient.get()
            .uri(URL + transactionId.toString())
            .retrieve()
            .bodyToMono(TransactionDto::class.java)
            .timeout(Duration.ofSeconds(50))
            .block()
    }

    fun postRequest(): TransactionDto? {
        return webClient
            .post()
            .uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(mapOf("clientSecret" to CLIENT_SECRET)))
            .retrieve()
            .onStatus(
                { obj: HttpStatus -> obj.is4xxClientError },
                { response: ClientResponse ->
                    eventLogger!!.error(
                        PaymentServiceNotableEvents.I_EXTERNAL_CLIENT_ERROR, response.bodyToMono(
                            Any::class.java
                        )
                    )
                    Mono.error(TooManyParallelRequestException("Too many parallel requests exception"))
                }
            )
            .onStatus(
                { obj: HttpStatus -> obj.is5xxServerError },
                { response: ClientResponse ->
                    eventLogger!!.error(
                        PaymentServiceNotableEvents.I_EXTERNAL_SYSTEM_ERROR,
                        response.rawStatusCode(),
                        response.bodyToMono(
                            TransactionDto::class.java
                        )
                    )
                    Mono.error(ExternalServiceException("External service exception", response.rawStatusCode()))
                }
            )
            .bodyToMono<TransactionDto>(TransactionDto::class.java)
            .timeout(Duration.ofSeconds(50))
            .retryWhen(
                Retry.fixedDelay(ATTEMPTS_LIMIT.toLong(), Duration.ofSeconds(5))
                    .filter { throwable: Throwable? -> throwable is ExternalServiceException })
            .block()
    }

    init {
        webClient = WebClient.create()
    }
}