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
import java.net.http.*
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
    private val postToken = mapOf("clientSecret" to "7d65037f-e9af-433e-8e3f-a3da77e019b1")
    private val objectMapper = ObjectMapper()
    private val postBody: String = objectMapper.writeValueAsString(postToken)
    @OptIn(ExperimentalTime::class)

    private fun getPostHeaders(body:String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create("http://77.234.215.138:30027/transactions"))
            .timeout(Duration.seconds(10).toJavaDuration())
            .header("Content-Type","application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
    }

    private fun processErrorCode(response: HttpResponse<String>){
        val responseJson = JSONObject(response.body())
        when(response.statusCode()){
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

//
//    private fun pollExternalSystem(id:String){
//        val client = HttpClient.newBuilder().build()
//        val request=HttpRequest.newBuilder()
//            .uri(URI.create("http://localhost:8080/transactions/${id}"))
//            .build()
//        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
//
//        if(response.statusCode()==200){
//            processSuccessCode(response)
//        }
//        else{
//            log.info("Error with code 404:")
//            if(response.statusCode()==404)return
//            //sleep 5s and then  poll for result
//            Thread.sleep(5000)
//            pollExternalSystem(id)
//        }
//    }

    fun callExternalSystem(){
        val client = HttpClient.newBuilder().build()
        try{
            val response= client.send(getPostHeaders(postBody), HttpResponse.BodyHandlers.ofString())
            if(response.statusCode()==200){
                processSuccessCode(response)
            }
            else{
                // Something is wrong
                processErrorCode(response)
                Thread.sleep(5000)
                callExternalSystem()
            }
        }catch (e:HttpConnectTimeoutException){
            //timeout, try again
            log.info("Request timeout!")
            callExternalSystem()
        }


    }

}