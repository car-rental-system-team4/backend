package com.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComplaintResolutionRequest {

	@NotBlank(message = "Admin response is required")
	private String adminResponse;

}
