package com.carrental.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class UserAnalyticsResponse {

	private Long totalUsers;
	private Long activeUsers; // Users with at least one booking
	private Long newUsersThisMonth;
	private Map<String, Long> usersByRole; // CUSTOMER, VENDOR, ADMIN
	private List<TopCustomer> topCustomers;
	private Double averageBookingsPerUser;

	@Data
	public static class TopCustomer {
		private Integer userId;
		private String userName;
		private String email;
		private Long bookingCount;
		private Double totalSpent;
	}

}
