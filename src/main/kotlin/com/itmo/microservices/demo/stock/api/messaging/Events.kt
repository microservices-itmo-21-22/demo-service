package com.itmo.microservices.demo.stock.api.messaging

import com.itmo.microservices.demo.stock.api.model.StockItemModel

data class StockItemCreatedEvent(val task: StockItemModel)

data class StockItemReservedEvent(val task: StockItemModel)

data class StockItemDeletedEvent(val task: StockItemModel)