package com.itmo.microservices.demo.delivery.api.service

import com.itmo.microservices.demo.delivery.impl.entity.DeliveryInfoRecord
import com.itmo.microservices.demo.delivery.impl.service.Timer
import com.itmo.microservices.demo.order.api.model.OrderDto

interface DeliveryService{
    fun getSlots(number:Int):List<Int>
    fun getDeliveryHistoryById(transactionId:String):List<DeliveryInfoRecord>
    fun delivery(order:OrderDto)
    fun delivery(order:OrderDto,times:Int)
}