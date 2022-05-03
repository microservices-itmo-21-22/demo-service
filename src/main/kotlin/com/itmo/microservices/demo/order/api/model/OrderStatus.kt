package com.itmo.microservices.demo.order.api.model

enum class OrderStatus {
    COLLECTING,
    DISCARD,
    BOOKED,
    PAID,
    SHIPPING,
    REFUND,
    COMPLETED
}