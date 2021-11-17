package com.itmo.microservices.demo.shoppingCartService.impl

class ShoppingCartStatus {
    companion object {
        fun active(): String = "ACTIVE"
        fun closed() : String = "CLOSED"
    }
}