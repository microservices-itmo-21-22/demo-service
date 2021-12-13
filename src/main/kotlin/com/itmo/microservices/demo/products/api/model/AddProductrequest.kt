package com.itmo.microservices.demo.products.api.model

data class AddProductrequest(
    val title:String ,
    val description:String,
    val price: Int,
    val amount: Int
)