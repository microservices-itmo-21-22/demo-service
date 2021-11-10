package com.itmo.microservices.demo.ShoppingCartService.impl

class ShoppingCartStatus {
    companion object {
        fun active(): String = "ACTIVE"
        fun closed() : String = "CLOSED"
    }
}