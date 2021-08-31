package com.itmo.microservices.demo.common.logging

interface NotableEvent {
    fun getTemplate(): String
    fun getName(): String
}