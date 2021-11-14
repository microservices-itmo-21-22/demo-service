package com.itmo.microservices.demo.bombardier.external.storage

import com.itmo.microservices.demo.bombardier.flow.BookingLogRecord
import com.itmo.microservices.demo.bombardier.flow.CatalogItem
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class ItemStorage {
    val items: ConcurrentHashMap<UUID, Pair<CatalogItem, Mutex>> = listOf(
        CatalogItem(title = "Socks", amount = 1),
        CatalogItem(title = "Book", amount = Int.MAX_VALUE),
        CatalogItem(title = "Plate", amount = Int.MAX_VALUE),
        CatalogItem(title = "Table", amount = Int.MAX_VALUE),
        CatalogItem(title = "Chair", amount = Int.MAX_VALUE),
        CatalogItem(title = "Watch", amount = Int.MAX_VALUE),
        CatalogItem(title = "Bed", amount = Int.MAX_VALUE)
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