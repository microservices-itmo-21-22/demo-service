package com.itmo.microservices.demo.bombardier.external.storage

import com.itmo.microservices.demo.bombardier.external.BookingLogRecord
import com.itmo.microservices.demo.bombardier.external.CatalogItem
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

//@Component
class ItemStorage {
    val items: ConcurrentHashMap<UUID, Pair<CatalogItem, Mutex>> = listOf(
        CatalogItem(id = UUID.randomUUID(), description = "", title = "Socks", amount = 1),
        CatalogItem(id = UUID.randomUUID(), description = "", title = "Book", amount = Int.MAX_VALUE),
        CatalogItem(id = UUID.randomUUID(), description = "", title = "Plate", amount = Int.MAX_VALUE),
        CatalogItem(id = UUID.randomUUID(), description = "", title = "Table", amount = Int.MAX_VALUE),
        CatalogItem(id = UUID.randomUUID(), description = "", title = "Chair", amount = Int.MAX_VALUE),
        CatalogItem(id = UUID.randomUUID(), description = "", title = "Watch", amount = Int.MAX_VALUE),
        CatalogItem(id = UUID.randomUUID(), description = "", title = "Bed", amount = Int.MAX_VALUE)
    ).map { it.id to (it to Mutex()) }.toMap(ConcurrentHashMap<UUID, Pair<CatalogItem, Mutex>>())

    val bookingRecords = ConcurrentLinkedQueue<BookingLogRecord>() // todo sukhoa should be moved to separate class

    suspend fun getBookingRecordsById(bookingId: UUID): List<BookingLogRecord> {
        return bookingRecords.filter { it.bookingId == bookingId }
    }

    suspend fun create(item: CatalogItem): CatalogItem {
        val existing = items.putIfAbsent(item.id, item to Mutex())
        if (existing != null) {
            throw IllegalArgumentException("Item already exists: $item")
        }
        return item
    }

    suspend fun getAndUpdate(itemId: UUID, updateFunction: suspend (CatalogItem) -> CatalogItem): CatalogItem {
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