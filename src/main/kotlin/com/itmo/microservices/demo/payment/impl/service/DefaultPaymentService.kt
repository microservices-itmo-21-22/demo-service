package com.itmo.microservices.demo.payment.impl.service

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payment.api.service.PaymentService
import com.itmo.microservices.demo.payment.api.util.FinancialOperationType
import com.itmo.microservices.demo.payment.impl.entity.Payment
import com.itmo.microservices.demo.payment.impl.logging.PaymentServiceNotableEvents
import com.itmo.microservices.demo.payment.impl.repository.PaymentRepository
import org.springframework.stereotype.Service
import java.sql.Time
import java.util.*
import kotlin.random.Random
import kong.unirest.Unirest
import kong.unirest.json.JSONObject
import com.itmo.microservices.demo.payment.impl.util.PaymentServiceMeta
import java.lang.StringBuilder

@Service
class DefaultPaymentService(private val paymentRepository: PaymentRepository) : PaymentService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    fun makeTransaction() : JSONObject {
        val url = PaymentServiceMeta.makeTransactionUri()

        val response = Unirest.post(url)
                .header("Content-Type", "application/json;IEEE754Compatible=true")
                .body("{\"clientSecret\": \"7d65037f-e9af-433e-8e3f-a3da77e019b1\"}")
                .asJson()

        val json = response.body.`object`

        if (response.status == 200) {
            return json
        }

        val sb = StringBuilder()

        sb.append(response.status.toString())
                .append(" ")
                .append(json.get("message").toString())
                .append(" at timestamp ")
                .append(json.get("timestamp").toString())

        eventLogger.error(PaymentServiceNotableEvents.I_MAKE_TRANSACTION_FAILURE, sb.toString())

        return JSONObject()
    }

    override fun getFinLog(orderId: UUID): List<UserAccountFinancialLogRecordDto> {
        val logs = mutableListOf<UserAccountFinancialLogRecordDto>()
        val orderPayments = paymentRepository.findByOrderId(orderId)

        orderPayments.forEach {
            logs.add(UserAccountFinancialLogRecordDto(FinancialOperationType.WITHDRAW, it.amount!!, it.orderId!!, UUID.randomUUID(), Random(0).nextLong()))
        }

        eventLogger.info(PaymentServiceNotableEvents.I_FINANCIAL_LOGS_GIVEN, orderId)

        return logs
    }

    override fun makePayment(orderId: UUID): PaymentSubmissionDto {

        val transaction = makeTransaction()

        if (transaction.isEmpty) {
            return PaymentSubmissionDto(0, UUID.fromString("0-0-0-0-0"))
        }

        val id = UUID.fromString(transaction.get("id").toString())
        val status = transaction.get("status").toString()

        if (status == "FAILURE") {
            return PaymentSubmissionDto(0, UUID.fromString("0-0-0-0-0"))
        }

        val cost = transaction.get("cost").toString().toInt()
        val submitTime = transaction.get("submitTime").toString().toLong()
        val completedTime = transaction.get("completedTime").toString().toLong()

        val submissionDto = PaymentSubmissionDto(completedTime, id)

        val payment = Payment()

        payment.orderId = orderId
        payment.transactionId = id
        payment.openTime = submitTime
        payment.closeTime = completedTime
        payment.type = 0
        payment.amount = cost

        paymentRepository.save(payment)

        eventLogger.info(PaymentServiceNotableEvents.I_PAYMENT_CREATED, submissionDto)

        return submissionDto
    }

}