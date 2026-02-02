package com.carrental.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.carrental.enums.BookingStatus;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bookings")
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_id", nullable = false)
	@JsonIgnore
	private Vehicle vehicle;

	@Column(name = "pickup_date", nullable = false)
	private LocalDate pickupDate;

	@Column(name = "return_date", nullable = false)
	private LocalDate returnDate;

	@Column(name = "pickup_location", nullable = false)
	private String pickupLocation;

	@Column(name = "return_location", nullable = false)
	private String returnLocation;

	@Column(name = "total_amount", nullable = false)
	private Double totalAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "ENUM('PENDING','CONFIRMED','ACTIVE','COMPLETED','CANCELLED')")
	private BookingStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (status == null) {
			status = BookingStatus.PENDING;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

}
