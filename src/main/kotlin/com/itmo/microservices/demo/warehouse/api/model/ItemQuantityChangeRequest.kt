package com.itmo.microservices.demo.warehouse.api.model

import java.util.*

data class ItemQuantityChangeRequest(var id: UUID, var amount: Int) {
}