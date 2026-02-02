package com.carrental.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class BookingAnalyticsResponse {

	private Long totalBookings;
	private Long pendingBookings;
	private Long confirmedBookings;
	private Long completedBookings;
	private Long cancelledBookings;
	private Map<String, Long> bookingsByStatus;
	private List<MonthlyBookings> monthlyBookings;
	private Double averageBookingDuration; // in days
	private Double cancellationRate; // percentage

	@Data
	public static class MonthlyBookings {
		private String month;
		private Integer year;
		private Long count;
	}

}
