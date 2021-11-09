package com.itmo.microservices.demo.notifications.impl.service

import com.itmo.microservices.demo.notifications.api.service.NotificationService
import com.itmo.microservices.demo.notifications.impl.repository.NotificationUserRepository
import com.itmo.microservices.demo.notifications.impl.entity.NotificationUser
import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.products.api.model.ProductModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StubNotificationService(private val userRepository: NotificationUserRepository) : NotificationService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(StubNotificationService::class.java)
    }

    override fun processNewUser(user: AppUserModel) {
        //userRepository.save(modelToEntity(user))
        log.info("User ${user.username} (${user.email}) was created & should be notified (but who cares)")
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
        username = user.username,
        name = user.name,
        email = user.email
    )
}