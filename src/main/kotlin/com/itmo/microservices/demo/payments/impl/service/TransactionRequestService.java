package com.itmo.microservices.demo.payments.impl.service;

import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.demo.payments.api.model.TransactionDto;
import com.itmo.microservices.demo.payments.api.model.TransactionStatus;
import com.itmo.microservices.demo.payments.impl.entity.Transaction;
import com.itmo.microservices.demo.payments.impl.exception.ExternalServiceException;
import com.itmo.microservices.demo.payments.impl.exception.TooManyParallelRequestException;
import com.itmo.microservices.demo.payments.impl.logging.PaymentServiceNotableEvents;
import com.itmo.microservices.demo.payments.impl.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;


@Service
public class TransactionRequestService {

//    p07-transaction: 8ddfb4e8-7f83-4c33-b7ac-8504f7c99205
//    p07-polling: 84ee19e4-f33f-4f60-80cb-765353745d10

    @InjectEventLogger
    private EventLogger eventLogger;

    private final String CLIENT_SECRET = "84ee19e4-f33f-4f60-80cb-765353745d10"; // p-07 polling

    private final String URL = "http://77.234.215.138:30027/transactions/";

    private final Integer ATTEMPTS_LIMIT = 5;

    private final WebClient webClient;

    private final TransactionRepository transactionRepository;

    public TransactionRequestService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.webClient = WebClient.create();
    }

    public TransactionDto poll(UUID transactionId) throws InterruptedException {
        System.out.println("Poll method. "
                + Thread.currentThread().getName());
        TransactionDto result;
        int delay = 100;
        while (true) {
            Thread.sleep(delay);
            result = requestTransactionResponse(transactionId);
            TransactionStatus status = result.getStatus();
            if (status != TransactionStatus.PENDING) {
                if (status == TransactionStatus.SUCCESS) {
                    eventLogger.info(PaymentServiceNotableEvents.I_EXTERNAL_SYSTEM_SUCCESS, result);
                } else {
                    eventLogger.info(PaymentServiceNotableEvents.I_EXTERNAL_SYSTEM_FAILURE, result);
                }
                Transaction transaction = new Transaction(
                        result.getId(),
                        result.getStatus(),
                        result.getSubmitTime(),
                        result.getCompletedTime(),
                        result.getCost(),
                        result.getDelta()
                );
                System.out.println(transaction.getStatus());
                transactionRepository.save(transaction);
                return result;
            }
            delay *= 2;
        }
    }

    public TransactionDto requestTransactionResponse(UUID transactionId) {
        return webClient.get()
                .uri(URL + transactionId.toString())
                .retrieve()
                .bodyToMono(TransactionDto.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    public TransactionDto postRequest() {
        return webClient
                .post()
                .uri(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Map.of("clientSecret", CLIENT_SECRET)), Map.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        response -> {
                            eventLogger.error(PaymentServiceNotableEvents.I_EXTERNAL_CLIENT_ERROR, response.bodyToMono(Object.class));
                            return Mono.error(new TooManyParallelRequestException("Too many parallel requests exception"));
                        }
                )
                .onStatus(HttpStatus::is5xxServerError,
                        (response) -> {
                            eventLogger.error(PaymentServiceNotableEvents.I_EXTERNAL_SYSTEM_ERROR, response.rawStatusCode(), response.bodyToMono(TransactionDto.class));
                            return Mono.error(new ExternalServiceException("External service exception", response.rawStatusCode()));
                        }
                )
                .bodyToMono(TransactionDto.class)
                .timeout(Duration.ofSeconds(50))
                .retryWhen(Retry.fixedDelay(ATTEMPTS_LIMIT, Duration.ofSeconds(5))
                        .filter(throwable -> throwable instanceof ExternalServiceException))
                .block();
    }

}
