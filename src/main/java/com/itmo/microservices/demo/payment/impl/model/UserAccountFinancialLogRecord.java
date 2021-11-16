package com.itmo.microservices.demo.payment.impl.model;

import com.itmo.microservices.demo.payment.api.model.FinancialOperationType;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public final class UserAccountFinancialLogRecord {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;
    @Column(columnDefinition = "uuid")
    private UUID userId;
    @Column(columnDefinition = "uuid")
    private UUID orderId; //TODO:: replace with related  entity
    @Column(columnDefinition = "uuid")
    private UUID paymentTransactionId; //TODO:: replace with related entity

    @Enumerated(EnumType.STRING)
    private FinancialOperationType type;
    private Integer amount;
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserAccountFinancialLogRecord that = (UserAccountFinancialLogRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
