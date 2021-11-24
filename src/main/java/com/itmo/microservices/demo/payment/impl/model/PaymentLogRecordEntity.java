package com.itmo.microservices.demo.payment.impl.model;

import com.itmo.microservices.demo.payment.api.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "payment_log_record")
public class PaymentLogRecordEntity {
	@Id
	@GeneratedValue
	@Column(columnDefinition = "uuid", updatable = false)
	private UUID id;
	@Column(columnDefinition = "uuid")
	private UUID transactionId;

	@Enumerated(EnumType.STRING)
	private PaymentStatus type;
	private Integer amount;
	private LocalDateTime timestamp;
}
