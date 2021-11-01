package com.itmo.microservices.demo.warehouse.impl.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;
import java.util.UUID;

@Entity
public class CatalogItem {
    @Id
    @GeneratedValue
    private UUID id;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private WarehouseItem warehouseItem;

    private String title;
    private String description;
    private Integer price = 100;

    public CatalogItem() {
    }

    public CatalogItem(UUID id,
                       WarehouseItem warehouseItem,
                       String title,
                       String description,
                       Integer price) {
        this.id = id;
        this.warehouseItem = warehouseItem;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public WarehouseItem getWarehouseItem() {
        return warehouseItem;
    }

    public void setWarehouseItem(WarehouseItem warehouseItem) {
        this.warehouseItem = warehouseItem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
