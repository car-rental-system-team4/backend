package com.carrental.dto;

import lombok.Data;

@Data
public class AdminStatsResponse {

	private Long totalUsers;
	private Long totalVehicles;
	private Long totalBookings;
	private Double totalRevenue;
	private Long pendingBookings;
	private Long completedBookings;
	private Long activeVehicles;
	private Long bookedVehicles;

}
