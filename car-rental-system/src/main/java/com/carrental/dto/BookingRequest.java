package com.carrental.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BookingRequest {

	@NotNull(message = "Vehicle ID is required")
	@Positive(message = "Vehicle ID must be positive")
	private Integer vehicleId;

	@NotNull(message = "Pickup date is required")
	@FutureOrPresent(message = "Pickup date must be today or in the future")
	private LocalDate pickupDate;

	@NotNull(message = "Return date is required")
	@Future(message = "Return date must be in the future")
	private LocalDate returnDate;

	@NotBlank(message = "Pickup location is required")
	private String pickupLocation;

	@NotBlank(message = "Return location is required")
	private String returnLocation;

}
