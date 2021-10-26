package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.order.api.service.BusketService
import com.itmo.microservices.demo.order.impl.repository.BusketRepository

class DefaultBusketService(private val busketRepository: BusketRepository): BusketService {

}