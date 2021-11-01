package com.itmo.microservices.demo.order.impl.entity

import com.itmo.microservices.demo.order.api.dto.OrderStatus
import lombok.Getter
import lombok.RequiredArgsConstructor
import lombok.Setter
import lombok.ToString
import org.hibernate.Hibernate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
class Order {
    @Id
    private val uuid: UUID? = null
    private val timeCreated: LocalDateTime? = null
    private val status: OrderStatus? = null

    @OneToMany
    @ToString.Exclude
    private val catalogItems: List<CatalogItem>? = null
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false
        val order = o as Order
        return uuid != null && uuid == order.uuid
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}