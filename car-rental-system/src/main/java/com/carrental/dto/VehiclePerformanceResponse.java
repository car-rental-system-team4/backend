package com.carrental.dto;

import java.util.List;

import lombok.Data;

@Data
public class VehiclePerformanceResponse {

	private List<TopVehicle> topVehicles;
	private Long totalVehicles;
	private Long availableVehicles;
	private Long bookedVehicles;
	private Long maintenanceVehicles;
	private Double averageUtilizationRate; // percentage

	@Data
	public static class TopVehicle {
		private Integer vehicleId;
		private String make;
		private String model;
		private Long bookingCount;
		private Double totalRevenue;
		private Double averageRating;
	}

}
