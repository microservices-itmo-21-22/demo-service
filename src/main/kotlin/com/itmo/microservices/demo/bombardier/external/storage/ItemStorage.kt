package com.itmo.microservices.demo.bombardier.external.storage

import com.itmo.microservices.demo.bombardier.flow.BookingLogRecord
import com.itmo.microservices.demo.bombardier.flow.Item
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ItemStorage {
    val items: ConcurrentHashMap<UUID, Pair<Item, Mutex>> = listOf(
        Item(title = "Socks", amount = 1),
        Item(title = "Book", amount = Int.MAX_VALUE),
        Item(title = "Plate", amount = Int.MAX_VALUE),
        Item(title = "Table", amount = Int.MAX_VALUE),
        Item(title = "Chair", amount = Int.MAX_VALUE),
        Item(title = "Watch", amount = Int.MAX_VALUE),
        Item(title = "Bed", amount = Int.MAX_VALUE)
    ).map { it.id to (it to Mutex()) }.toMap(ConcurrentHashMap<UUID, Pair<Item, Mutex>>())

    val bookingRecords: MutableList<BookingLogRecord> = mutableListOf() // todo sukhoa should be moved to separate class

    suspend fun getBookingRecordsById(bookingId: UUID): List<BookingLogRecord> {
        return bookingRecords.filter { it.bookingId == bookingId }
    }

    suspend fun create(item: Item): Item {
        val existing = items.putIfAbsent(item.id, item to Mutex())
        if (existing != null) {
            throw IllegalArgumentException("Item already exists: $item")
        }
        return item
    }

    suspend fun getAndUpdate(itemId: UUID, updateFunction: suspend (Item) -> Item): Item {
        val (_, mutex) = items[itemId] ?: throw IllegalArgumentException("No such item: $itemId")
        mutex.withLock {
            val (item, _) = items[itemId] ?: throw IllegalArgumentException("No such item: $itemId")
            val updatedItem = updateFunction(item)
            items[itemId] = updatedItem to mutex
            return updatedItem
        }
    }

    suspend fun get(itemId: UUID) = items[itemId]?.first ?: throw IllegalArgumentException("No such item: $itemId")
}