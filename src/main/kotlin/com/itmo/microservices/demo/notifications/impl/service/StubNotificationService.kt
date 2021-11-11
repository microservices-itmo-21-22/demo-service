package com.itmo.microservices.demo.notifications.impl.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.itmo.microservices.demo.notifications.api.service.NotificationService
import com.itmo.microservices.demo.notifications.impl.entity.NotificationUser
import com.itmo.microservices.demo.notifications.impl.repository.NotificationUserRepository
import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.products.api.model.ProductModel
import com.itmo.microservices.demo.users.api.model.AppUserModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpTimeoutException
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaDuration


@Service
class StubNotificationService(private val userRepository: NotificationUserRepository) : NotificationService {
    var count =0
    companion object {
        val log: Logger = LoggerFactory.getLogger(StubNotificationService::class.java)
    }

    override fun processNewUser(user: AppUserModel) {
        //userRepository.save(modelToEntity(user))
        log.info("User ${user.name}  was created & should be notified (but who cares)")
        val call = CallExternalSystemForNotification()
        call.callExternalSystem()
    }

    override fun processPayment(payment: PaymentModel) {
        //send email to user
        when(payment.status){
            0->log.info("Payment at ${payment.date},user: ${payment.username} successful")
            1->log.info("Payment at ${payment.date},user: ${payment.username} not successful")
        }
    }

    override fun processAddProduct(product: ProductModel) {
        // just for debugging
        log.info("Product 'ID:${product.id}  ${product.name}' ${product.price} added into database ")
    }

    private fun modelToEntity(user: AppUserModel): NotificationUser = NotificationUser(
        name = user.name,
    )
}


class CallExternalSystemForNotification {
    companion object {
        val log: Logger = LoggerFactory.getLogger(CallExternalSystemForNotification::class.java)
    }
    private val token = mapOf("clientSecret" to "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private val objectMapper = ObjectMapper()
    private val requestBody: String = objectMapper.writeValueAsString(token)
    @OptIn(ExperimentalTime::class)

    private fun getPostHeaders(body:String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/transactions"))
            .timeout(Duration.seconds(60).toJavaDuration())
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
    }

    private fun processErrorCode(response: HttpResponse<String>){
        val responseJson = JSONObject(response.body())
        when(response.statusCode()){
            404->{
                log.info("error with code 404: page not found or there is no transaction with the specified ID.")
            }
            500->{
                log.info("error with code 500: transaction not started due to server error")
            }
            401->{
                log.info("error with code 401: authentication error when call external system")
            }
            429->{
                log.info("error with code 429: number of requests exceeded")
            }
        }
        log.info("time: ${responseJson.getLong("timestamp")}  message: ${responseJson.getString("message")}")
    }

    private fun processSuccessCode(response: HttpResponse<String>){
        val responseJson = JSONObject(response.body())
        when(responseJson.getString("status")){
            "SUCCESS"->{
                log.info("Transaction success: id:${responseJson.getString("id")},completedTime${responseJson.getLong("completedTime")}")
                return
            }
            "PENDING"->{
                //Sleep for 5 seconds and then poll
                Thread.sleep(5000)
                pollExternalSystem(responseJson.getString("id"))
            }
            "FAILURE"->{
                //retry post request
                try {
                    callExternalSystem()
                }catch (e:HttpTimeoutException){
                    log.info("Exception： HttpTimeoutException")
                    //retry once
                    callExternalSystem()
                }
            }
        }
    }

    private fun pollExternalSystem(id:String){
        val client = HttpClient.newBuilder().build()
        val request=HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/transactions/${id}"))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if(response.statusCode()==200){
            processSuccessCode(response)
        }
        else{
            log.info("Error with code 404:")
            if(response.statusCode()==404)return
            //sleep 5s and then  poll for result
            Thread.sleep(5000)
            pollExternalSystem(id)
        }
    }


    fun callExternalSystem(){
        val client = HttpClient.newBuilder().build()
        val response = client.send(getPostHeaders(requestBody), HttpResponse.BodyHandlers.ofString())
        if(response.statusCode()==200){
            processSuccessCode(response)
        }
        else{
            // Something is wrong
            processErrorCode(response)
            Thread.sleep(5000)
            callExternalSystem()
        }

    }

}