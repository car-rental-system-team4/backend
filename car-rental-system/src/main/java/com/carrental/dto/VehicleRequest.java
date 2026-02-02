package com.carrental.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class VehicleRequest {

	@NotBlank(message = "Make is required")
	private String make;

	@NotBlank(message = "Model is required")
	private String model;

	@NotNull(message = "Year is required")
	@Min(value = 1900, message = "Year must be after 1900")
	private Integer year;

	@NotBlank(message = "Color is required")
	private String color;

	@NotBlank(message = "License plate is required")
	private String licensePlate;

	@NotBlank(message = "VIN is required")
	private String vin;

	@NotNull(message = "Price per day is required")
	@Positive(message = "Price per day must be positive")
	private Double pricePerDay;

	@NotBlank(message = "Fuel type is required")
	private String fuelType;

	@NotBlank(message = "Transmission is required")
	private String transmission;

	@NotNull(message = "Seating capacity is required")
	@Min(value = 1, message = "Seating capacity must be at least 1")
	private Integer seatingCapacity;

	private String description;

	private MultipartFile image;

	private String imageUrl; // Keep for backward compatibility or when not updating image

}
