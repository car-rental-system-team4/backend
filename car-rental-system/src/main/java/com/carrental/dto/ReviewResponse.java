package com.carrental.dto;

import java.time.LocalDateTime;

import com.carrental.enums.ReviewStatus;

import lombok.Data;

@Data
public class ReviewResponse {

	private Integer id;
	private Integer userId;
	private String userName;
	private String userEmail;
	private Integer vehicleId;
	private String vehicleMake;
	private String vehicleModel;
	private Integer rating;
	private String comment;
	private ReviewStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
