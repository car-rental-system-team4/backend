package com.carrental.dto;

import java.time.LocalDateTime;

import com.carrental.enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentResponse {

	private Integer id;
	private Integer bookingId;
	private Double amount;
	private String paymentMethod;
	private PaymentStatus status;
	private String transactionId;
	private LocalDateTime paymentDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Integer userId;
	private String userName;
	private Integer vehicleId;
	private String vehicleMake;
	private String vehicleModel;

}
