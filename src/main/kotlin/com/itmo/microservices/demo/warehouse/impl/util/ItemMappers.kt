package com.itmo.microservices.demo.warehouse.impl.util

import com.itmo.microservices.demo.warehouse.api.model.Item
import com.itmo.microservices.demo.warehouse.impl.entity.ItemEntity

fun ItemEntity.toModel(): Item = Item(id, title, description, price, amount)
