package com.itmo.microservices.demo.bombardier.external.communicator

import org.springframework.boot.configurationprocessor.json.JSONObject

fun JSONObject.withItems(vararg items: Pair<String, String>): JSONObject {
    for (item in items) {
        put(item.first, item.second)
    }

    return this
}