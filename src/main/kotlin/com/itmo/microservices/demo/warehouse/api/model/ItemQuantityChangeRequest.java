package com.itmo.microservices.demo.warehouse.api.model;

import java.util.UUID;

public class ItemQuantityChangeRequest {
    UUID id;
    Integer amount;

    public ItemQuantityChangeRequest() {}

    public ItemQuantityChangeRequest(UUID id, Integer amount) {
        this.id = id;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
