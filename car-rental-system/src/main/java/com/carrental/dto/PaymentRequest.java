package com.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {

	@NotNull(message = "Booking ID is required")
	@Positive(message = "Booking ID must be positive")
	private Integer bookingId;

	@NotBlank(message = "Payment method is required")
	private String paymentMethod;

	private String transactionId;

}
