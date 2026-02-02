package com.carrental.entity;

import java.time.LocalDateTime;

import com.carrental.enums.VehicleStatus;
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
@Table(name = "vehicles")
public class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String make;

	@Column(name = "manufacturer", nullable = false)
	private String manufacturer;

	@Column(nullable = false)
	private String model;

	@Column(nullable = false)
	private Integer year;

	@Column(nullable = false)
	private String color;

	@Column(name = "license_plate", nullable = false, unique = true)
	private String licensePlate;

	@Column(nullable = false, unique = true)
	private String vin;

	@Column(name = "price_per_day", nullable = false)
	private Double pricePerDay;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "ENUM('AVAILABLE','BOOKED','UNDER_MAINTENANCE','DEACTIVATED')")
	private VehicleStatus status;

	@Column(name = "fuel_type", nullable = false)
	private String fuelType;

	@Column(nullable = false)
	private String transmission;

	@Column(name = "seating_capacity", nullable = false)
	private Integer seatingCapacity;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "image_url")
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vendor_id", nullable = false)
	@JsonIgnore
	private User vendor;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (status == null) {
			status = VehicleStatus.AVAILABLE;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

}
