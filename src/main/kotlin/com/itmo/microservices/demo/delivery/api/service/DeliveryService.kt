package com.itmo.microservices.demo.delivery.api.service

interface DeliveryService{
    fun getSlots(number:Int):List<Int>
    fun getDeliveryHistoryById(id:String)
}