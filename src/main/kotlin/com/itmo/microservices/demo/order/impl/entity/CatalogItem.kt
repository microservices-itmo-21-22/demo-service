package com.itmo.microservices.demo.order.impl.entity

import lombok.Getter
import lombok.RequiredArgsConstructor
import lombok.Setter
import lombok.ToString
import org.hibernate.Hibernate
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
class CatalogItem {
    @Id
    @GeneratedValue
    private val uuid: UUID? = null
    private val title: String? = null
    private val description: String? = null
    private val price = 0
    private val amount = 0

    @ManyToOne
    private val order: Order? = null
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false
        val that = o as CatalogItem
        return uuid != null && uuid == that.uuid
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}