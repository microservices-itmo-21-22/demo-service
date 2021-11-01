package com.itmo.microservices.demo.order.impl.entity;

import com.itmo.microservices.demo.order.api.dto.OrderStatus;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Order {
    @Id
    private UUID uuid;

    private LocalDateTime timeCreated;
    private OrderStatus status;

    private Timestamp deliveryInfo;
    @OneToMany
    @ToString.Exclude
    private List<CatalogItem> catalogItems;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Order order = (Order) o;
        return uuid != null && Objects.equals(uuid, order.uuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
