package com.itmo.microservices.demo.notifications

import com.itmo.microservices.demo.DemoServiceApplication
import com.itmo.microservices.demo.notifications.api.service.NotificationService
import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.products.api.model.ProductModel
import com.itmo.microservices.demo.products.api.model.ProductType
import com.itmo.microservices.demo.users.api.model.AppUserModel
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*



@ActiveProfiles("dev")
@SpringBootTest(classes = [DemoServiceApplication::class])
class NotificationServiceTest {
    @Autowired
    var notifi: NotificationService?= null

    @Autowired(required = false)
    var user: AppUserModel = AppUserModel( username="tq",name="tian",surname="tt",email="123@qq.com",password="123")
    var payment : PaymentModel=PaymentModel( id= UUID.randomUUID(),date=Date(0),status=1,username="hello")
    var pro : ProductModel=ProductModel(id= UUID.randomUUID(),name="milk",description="String?",country="String?",price=12.1,sale=13.1,type= ProductType.OTHER)
    @Test
    fun processNewUser(){
        var res : Unit? = notifi?.processNewUser(user)
        print(res)
    }

    @Test
    fun processPayment(){
        var res : Unit? = notifi?.processPayment(payment)
        print(res)
    }
    @Test
    fun processAddProduct(){
        var res : Unit? = notifi?.processAddProduct(pro)
        print(res)
    }
}