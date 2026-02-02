package com.carrental.entity;

import java.time.LocalDateTime;

import com.carrental.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_id", nullable = false, unique = true)
	@JsonIgnore
	private Booking booking;

	@Column(name = "amount", nullable = false)
	private Double amount;

	@Column(name = "payment_method", nullable = false)
	private String paymentMethod;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "ENUM('PENDING','COMPLETED','FAILED','REFUNDED')")
	private PaymentStatus status;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "payment_date")
	private LocalDateTime paymentDate;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (status == null) {
			status = PaymentStatus.PENDING;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

}
