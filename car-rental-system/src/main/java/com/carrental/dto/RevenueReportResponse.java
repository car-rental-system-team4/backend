package com.carrental.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class RevenueReportResponse {

	private Double totalRevenue;
	private Double monthlyRevenue;
	private Double yearlyRevenue;
	private List<MonthlyRevenue> monthlyBreakdown;
	private Map<String, Double> revenueByStatus; // COMPLETED, PENDING, etc.
	private Double averageBookingValue;
	private Integer totalTransactions;

	@Data
	public static class MonthlyRevenue {
		private String month;
		private Integer year;
		private Double revenue;
		private Integer bookingCount;
	}

}
