package com.itmo.microservices.demo.notifications.impl.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.itmo.microservices.demo.notifications.api.service.NotificationService
import com.itmo.microservices.demo.notifications.impl.repository.NotificationUserRepository
import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.products.api.model.CatalogItemDto
import com.itmo.microservices.demo.users.api.model.AppUserModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.net.ConnectException
import java.net.URI
import java.net.http.*
import java.util.concurrent.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaDuration


@Service
class StubNotificationService(private val userRepository: NotificationUserRepository) : NotificationService {

    @Autowired
    var emailSender:SendEmailToUser?=null

    companion object {
        val log: Logger = LoggerFactory.getLogger(StubNotificationService::class.java)
    }

    override fun processNewUser(user: AppUserModel) {
        log.info("User ${user.name}  was created & should be notified (but who cares)")
        emailSender?.sendEmailToUser(user.name)

    }

    override fun processPayment(payment: PaymentModel) {
        when(payment.status){
            0->{
                log.info("Payment at ${payment.date},user: ${payment.username} successful")
            }
            1->{
                log.info("Payment at ${payment.date},user: ${payment.username} not successful")
            }
        }
    }

    override fun processAddProduct(product: CatalogItemDto) {
        // just for debugging
        log.info("Product 'ID:${product.id}  ${product.name}' ${product.price} added into database ")
    }

}

@Service
class SendEmailToUser(){
    private val postToken = mapOf("clientSecret" to "8ddfb4e8-7f83-4c33-b7ac-8504f7c99205")
    private val objectMapper = ObjectMapper()
    private val postBody: String = objectMapper.writeValueAsString(postToken)
    @OptIn(ExperimentalTime::class)
    private val timeout = Duration.seconds(10).toJavaDuration()
    val httpClient: HttpClient = HttpClient.newBuilder().build()

    private fun getPostHeaders(body:String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create("http://77.234.215.138:30027/transactions/"))
            .header("Content-Type","application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
    }


    companion object {
        val log: Logger = LoggerFactory.getLogger(StubNotificationService::class.java)
    }

    @Autowired
    private var executorService: ExecutorService? = null


    fun sendEmailToUser(message:String) {
        try{
            executorService?.submit(callExternalSystem(message))
        }catch(e:RejectedExecutionException){
            log.info(e.message)
            Thread.sleep(3000)
            executorService?.submit(callExternalSystem(message))
        }
        catch(e2:ConnectException){
            log.info(e2.message)
            executorService?.submit(callExternalSystem(message))
        }
        catch (e3:HttpTimeoutException){
            log.info("Exception： HttpTimeoutException")
            //retry once
            executorService?.submit(callExternalSystem(message))
        }
    }

    fun callExternalSystem(message:String): Runnable {
        return Runnable {
            try{
                val response = httpClient.send(getPostHeaders(postBody), HttpResponse.BodyHandlers.ofString())
                if(response.statusCode()==200){
                    processSuccessCode(response,message)
                }
                else{
                    // Something is wrong
                    processErrorCode(response,message)
                    Thread.sleep(3000)
                    sendEmailToUser(message)
                }
            }catch (e:HttpConnectTimeoutException){
                //timeout, try again
                log.info("Request timeout!")
                sendEmailToUser(message)
            }
        }
    }

    fun processErrorCode(response: HttpResponse<String>,  message:String){
        val responseJson = JSONObject(response.body())
        when(response.statusCode()){
            500->{
                log.info("user: ${message},error with code 500: transaction not started due to server error")
            }
            401->{
                log.info("user: ${message},error with code 401: authentication error when call external system")
            }
            429->{
                log.info("user: ${message},error with code 429: number of requests exceeded")
            }
        }
        log.info("user: ${message},time: ${responseJson.getLong("timestamp")}  message: ${responseJson.getString("message")}")
    }

    fun processSuccessCode(response: HttpResponse<String>, message:String){
        val responseJson = JSONObject(response.body())
        log.info("$message  "+responseJson.getString("status"))
        when(responseJson.getString("status")){
            "SUCCESS"->{
               log.info("user: ${message},Transaction success: id:${responseJson.getString("id")},completedTime${responseJson.getLong("completedTime")}")
                return
            }
            "FAILURE"->{
                //retry post request
                sendEmailToUser(message)
            }
        }
    }
}


@Configuration
class ThreadPoolConfig {
    fun executorService(): ExecutorService {
        return ThreadPoolExecutor(
            9,
            17,
            1L,
            TimeUnit.MINUTES,
            ArrayBlockingQueue(1024)
        )
    }
}

