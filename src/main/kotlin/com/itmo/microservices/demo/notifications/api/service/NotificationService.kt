package com.itmo.microservices.demo.notifications.api.service

import com.itmo.microservices.demo.tasks.api.model.TaskModel
import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.order.api.model.OrderModel
import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.products.api.model.ProductModel

interface NotificationService {
    fun processNewUser(user: AppUserModel)
    fun processPayment(payment:PaymentModel)
    fun processAddProduct(product:ProductModel)
}