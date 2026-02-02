package com.carrental.dto;

import java.time.LocalDateTime;

import com.carrental.enums.VehicleStatus;

import lombok.Data;

@Data
public class VehicleResponse {

	private Integer id;
	private String make;
	private String model;
	private Integer year;
	private String color;
	private String licensePlate;
	private String vin;
	private Double pricePerDay;
	private VehicleStatus status;
	private String fuelType;
	private String transmission;
	private Integer seatingCapacity;
	private String description;
	private String imageUrl;
	private Integer vendorId;
	private String vendorName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
