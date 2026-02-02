package com.carrental.service;

import java.util.List;

import com.carrental.dto.AdminStatsResponse;
import com.carrental.dto.BookingResponse;
import com.carrental.dto.PaymentResponse;
import com.carrental.dto.VehicleResponse;
import com.carrental.entity.User;

public interface AdminService {

	AdminStatsResponse getAdminStats();

	List<User> getAllUsers();

	List<User> getPendingUsers();

	List<BookingResponse> getAllBookings();

	List<PaymentResponse> getAllPayments();

	List<VehicleResponse> getAllVehicles();

	User approveUser(Integer userId);

	User rejectUser(Integer userId);

	void deleteUser(Integer userId);

	List<com.carrental.dto.ReviewResponse> getAllReviews();

	List<com.carrental.dto.ReviewResponse> getReviewsByStatus(com.carrental.enums.ReviewStatus status);

	com.carrental.dto.RevenueReportResponse getRevenueReport(String period);

	com.carrental.dto.BookingAnalyticsResponse getBookingAnalytics();

	com.carrental.dto.VehiclePerformanceResponse getVehiclePerformance();

	com.carrental.dto.UserAnalyticsResponse getUserAnalytics();

}
