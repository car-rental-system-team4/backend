package com.carrental.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.carrental.enums.BookingStatus;
import lombok.Data;

@Data
public class BookingResponse {

	private Integer id;
	private Integer userId;
	private String userName;
	private String userEmail;
	private Integer vehicleId;
	private String vehicleMake;
	private String vehicleModel;
	private Integer vehicleYear;
	private String vehicleLicensePlate;
	private Double vehiclePricePerDay;
	private String vehicleImageUrl;
	private LocalDate pickupDate;
	private LocalDate returnDate;
	private String pickupLocation;
	private String returnLocation;
	private Double totalAmount;
	private BookingStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Integer vendorId;
	private String vendorName;

}
