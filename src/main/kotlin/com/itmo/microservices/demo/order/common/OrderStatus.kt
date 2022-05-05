package com.itmo.microservices.demo.order.common

enum class OrderStatus {
    COLLECTING,
    DISCARD,
    BOOKED,
    PAID,
    SHIPPING,
    REFUND,
    COMPLETED
}