package com.carrental.dto;

import java.time.LocalDateTime;

import com.carrental.enums.ComplaintStatus;

import lombok.Data;

@Data
public class ComplaintResponse {

	private Integer id;
	private Integer userId;
	private String userName;
	private String userEmail;
	private String subject;
	private String description;
	private Integer bookingId;
	private ComplaintStatus status;
	private String adminResponse;
	private LocalDateTime resolvedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
