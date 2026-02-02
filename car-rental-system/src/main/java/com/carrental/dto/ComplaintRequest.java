package com.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplaintRequest {

	@NotBlank(message = "Subject is required")
	private String subject;

	@NotBlank(message = "Description is required")
	private String description;

	private Integer bookingId; // Optional

}
