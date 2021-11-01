package com.itmo.microservices.demo.warehouse.impl.service;

import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.demo.warehouse.api.model.ItemQuantityChangeRequest;
import com.itmo.microservices.demo.warehouse.api.model.ResponseMessage;
import com.itmo.microservices.demo.warehouse.impl.entity.CatalogItem;
import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItem;
import com.itmo.microservices.demo.warehouse.impl.logging.WarehouseServiceNotableEvents;
import com.itmo.microservices.demo.warehouse.impl.repository.CatalogItemRepository;
import com.itmo.microservices.demo.warehouse.impl.repository.WarehouseItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.itmo.microservices.commonlib.logging.EventLogger;

import java.util.List;
import java.util.UUID;

@Service
public class WarehouseService {

    private final WarehouseItemRepository warehouseRepository;
    private final CatalogItemRepository catalogRepository;

    @InjectEventLogger
    private EventLogger eventLogger;

    private boolean ValidationError(){
        return false;
    }


    public WarehouseService(WarehouseItemRepository warehouseRepository, CatalogItemRepository catalogRepository) {
        this.warehouseRepository = warehouseRepository;
        this.catalogRepository = catalogRepository;
    }

    public ResponseEntity<String> income(ItemQuantityChangeRequest values){
        if(!catalogRepository.existsById(values.getId()) || values.getAmount() < 1)
            return new ResponseEntity<>( ResponseMessage.BAD_REQUEST.TEXT(), HttpStatus.BAD_REQUEST);
        else {
            WarehouseItem item = warehouseRepository.findWarehouseItemById(values.getId());
            item.setAmount(item.getAmount() + values.getAmount());

            eventLogger.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.getId());
            warehouseRepository.save(item);
            return new ResponseEntity<>( ResponseMessage.OK_UPDATED.TEXT(), HttpStatus.OK);
        }
    }

    public ResponseEntity<String> outcome(ItemQuantityChangeRequest values){
        if(!catalogRepository.existsById(values.getId()) || values.getAmount() < 1)
            return new ResponseEntity<>( ResponseMessage.BAD_REQUEST.TEXT(), HttpStatus.BAD_REQUEST);
        else {
            WarehouseItem item = warehouseRepository.findWarehouseItemById(values.getId());

            if(item.getAmount() >= values.getAmount())
                item.setAmount(item.getAmount() - values.getAmount());
            else
                return new ResponseEntity<>( ResponseMessage.BAD_QUANTITY.TEXT(), HttpStatus.BAD_REQUEST);

            eventLogger.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.getId());
            warehouseRepository.save(item);
            return new ResponseEntity<>( ResponseMessage.OK_UPDATED.TEXT(), HttpStatus.OK);
        }
    }

    public ResponseEntity<String> book(ItemQuantityChangeRequest values){
        if(!catalogRepository.existsById(values.getId()) || values.getAmount() < 1)
            return new ResponseEntity<>( ResponseMessage.BAD_REQUEST.TEXT(), HttpStatus.BAD_REQUEST);
        else {
            WarehouseItem item = warehouseRepository.findWarehouseItemById(values.getId());

            if(item.getAmount() - item.getBooked() >= values.getAmount())
                item.setBooked(item.getBooked() + values.getAmount());
            else
                return new ResponseEntity<>( ResponseMessage.BAD_QUANTITY.TEXT(), HttpStatus.BAD_REQUEST);

            eventLogger.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.getId());
            warehouseRepository.save(item);
            return new ResponseEntity<>( ResponseMessage.OK_UPDATED.TEXT(), HttpStatus.OK);
        }
    }

    public ResponseEntity<String> unbook(ItemQuantityChangeRequest values){
        if(!catalogRepository.existsById(values.getId()) || values.getAmount() < 1)
            return new ResponseEntity<>( ResponseMessage.BAD_REQUEST.TEXT(), HttpStatus.BAD_REQUEST);
        else {
            WarehouseItem item = warehouseRepository.findWarehouseItemById(values.getId());

            if(item.getBooked() >= values.getAmount())
                item.setBooked(item.getBooked() - values.getAmount());
            else
                return new ResponseEntity<>( ResponseMessage.BAD_QUANTITY.TEXT(), HttpStatus.BAD_REQUEST);

            eventLogger.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.getId());
            warehouseRepository.save(item);
            return new ResponseEntity<>( ResponseMessage.OK_UPDATED.TEXT(), HttpStatus.OK);
        }
    }

    public ResponseEntity<String> addItem(CatalogItem item){
        if(ValidationError())
            return new ResponseEntity<>( ResponseMessage.BAD_REQUEST.TEXT(), HttpStatus.BAD_REQUEST);

        CatalogItem catalogItem = catalogRepository.save(item);
        WarehouseItem warehouseItem = new WarehouseItem(catalogItem, 0, 0);

        eventLogger.info(WarehouseServiceNotableEvents.I_ITEM_CREATED, catalogItem.getId());
        warehouseRepository.save(warehouseItem);

        return new ResponseEntity<>( ResponseMessage.OK_CREATED.TEXT(), HttpStatus.OK);
    }

    public ResponseEntity<List<CatalogItem>> getItemsList() {

        List<CatalogItem> list = catalogRepository.findAll();

        return new ResponseEntity<>( list, HttpStatus.OK);
    }

    public ResponseEntity<CatalogItem> getItem(UUID id) {
        return new ResponseEntity<>( catalogRepository.findCatalogItemById(id), HttpStatus.OK);
    }

    public ResponseEntity<WarehouseItem> getItemQuantity(UUID id) {
        return new ResponseEntity<>( warehouseRepository.findWarehouseItemById(id), HttpStatus.OK);
    }
}
