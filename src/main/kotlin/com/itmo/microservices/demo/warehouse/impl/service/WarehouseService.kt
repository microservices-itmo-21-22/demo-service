package com.itmo.microservices.demo.warehouse.impl.service

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.warehouse.api.model.ItemQuantityChangeRequest
import com.itmo.microservices.demo.warehouse.api.model.ResponseMessage
import com.itmo.microservices.demo.warehouse.impl.entity.WCatalogItem
import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItem
import com.itmo.microservices.demo.warehouse.impl.logging.WarehouseServiceNotableEvents
import com.itmo.microservices.demo.warehouse.impl.repository.ICatalogItemRepository
import com.itmo.microservices.demo.warehouse.impl.repository.WarehouseItemRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class WarehouseService(
    private val warehouseRepository: WarehouseItemRepository,
    private val catalogRepository: ICatalogItemRepository
) {
    @InjectEventLogger
    private val eventLogger: EventLogger? = null
    private fun ValidationError(): Boolean {
        return false
    }

    fun income(values: ItemQuantityChangeRequest): ResponseEntity<String> {
        val item = warehouseRepository.findWarehouseItemById(values.id)
        //Здесь чека на values.amount не должно быть. он должен быть в контроллере.
        if ((item == null) || (values.amount < 1)) {
            //Так ошибки нельзя описывать, сделай лучше какой нибудь интерфейс или че нить подобное data class Error(val errorCode:Int, val message: String)
            return ResponseEntity(ResponseMessage.BAD_REQUEST.getText(), HttpStatus.BAD_REQUEST)
        }
        item.amount = item.amount + values.amount
        eventLogger!!.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.id)
        warehouseRepository.save(item)
        return ResponseEntity(ResponseMessage.OK_UPDATED.getText(), HttpStatus.OK)
    }

    fun outcome(values: ItemQuantityChangeRequest): ResponseEntity<String> {
        val item = warehouseRepository.findWarehouseItemById(values.id)

        if ((item == null) || (values.amount < 1)) {
            return ResponseEntity(
                ResponseMessage.BAD_REQUEST.getText(),
                HttpStatus.BAD_REQUEST
            )
        } else {
            if (item.amount >= values.amount) {
                item.amount =
                    item.amount - values.amount
            } else {
                return ResponseEntity(
                    ResponseMessage.BAD_QUANTITY.getText(),
                    HttpStatus.BAD_REQUEST
                )
            }
            eventLogger!!.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.id)
            warehouseRepository.save(item)
            return ResponseEntity(ResponseMessage.OK_UPDATED.getText(), HttpStatus.OK)
        }
    }

    fun book(values: ItemQuantityChangeRequest): ResponseEntity<String> {
        val item = warehouseRepository.findWarehouseItemById(values.id)

        if ((item == null) || (values.amount < 1)) {
            return ResponseEntity(
                ResponseMessage.BAD_REQUEST.getText(),
                HttpStatus.BAD_REQUEST
            )
        } else {
            if (item.amount - item.booked >= values.amount) item.booked =
                item.booked + values.amount else return ResponseEntity(
                ResponseMessage.BAD_QUANTITY.getText(),
                HttpStatus.BAD_REQUEST
            )
            eventLogger!!.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.id)
            warehouseRepository.save(item)
            return ResponseEntity(ResponseMessage.OK_UPDATED.getText(), HttpStatus.OK)
        }
    }

    fun unbook(values: ItemQuantityChangeRequest): ResponseEntity<String> {
        val item = warehouseRepository.findWarehouseItemById(values.id)

        if ((item == null) || (values.amount < 1)) {
            return ResponseEntity(
                ResponseMessage.BAD_REQUEST.getText(),
                HttpStatus.BAD_REQUEST
            )
        } else {
            if (item.booked >= values.amount) item.booked =
                item.booked - values.amount else return ResponseEntity(
                ResponseMessage.BAD_QUANTITY.getText(),
                HttpStatus.BAD_REQUEST
            )
            eventLogger!!.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.id)
            warehouseRepository.save(item)
            return ResponseEntity(ResponseMessage.OK_UPDATED.getText(), HttpStatus.OK)
        }
    }

    fun addItem(item: WCatalogItem): ResponseEntity<String> {
        if (ValidationError()) return ResponseEntity(ResponseMessage.BAD_REQUEST.getText(), HttpStatus.BAD_REQUEST)
        val catalogItem = catalogRepository.save(item)
        val warehouseItem = WarehouseItem(catalogItem, 0, 0)
        eventLogger!!.info(WarehouseServiceNotableEvents.I_ITEM_CREATED, catalogItem.id)
        warehouseRepository.save(warehouseItem)
        return ResponseEntity(ResponseMessage.OK_CREATED.getText(), HttpStatus.OK)
    }

    val itemsList: ResponseEntity<List<WCatalogItem>>
        get() {
            val list = catalogRepository.findAll()
            return ResponseEntity(list, HttpStatus.OK)
        }

    fun getItem(id: UUID?): ResponseEntity<WCatalogItem> {
        return ResponseEntity(catalogRepository.findCatalogItemById(id), HttpStatus.OK)
    }

    fun getItemQuantity(id: UUID?): ResponseEntity<WarehouseItem> {
        return ResponseEntity(warehouseRepository.findWarehouseItemById(id), HttpStatus.OK)
    }
}