package com.itmo.microservices.demo.orders.api.model

enum class OrderStatus {
    COLLECTING,
    DISCARD,
    BOOKED,
    PAID,
    SHIPPING,
    REFUND,
    COMPLETED
}
