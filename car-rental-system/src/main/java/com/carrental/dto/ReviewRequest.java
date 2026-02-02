package com.carrental.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {

	@NotNull(message = "Vehicle ID is required")
	private Integer vehicleId;

	@NotNull(message = "Rating is required")
	@Min(value = 1, message = "Rating must be between 1 and 5")
	@Max(value = 5, message = "Rating must be between 1 and 5")
	private Integer rating;

	private String comment;

}
