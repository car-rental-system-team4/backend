package com.carrental.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.carrental.dto.AdminStatsResponse;
import com.carrental.dto.BookingAnalyticsResponse;
import com.carrental.dto.BookingResponse;
import com.carrental.dto.PaymentResponse;
import com.carrental.dto.RevenueReportResponse;
import com.carrental.dto.ReviewResponse;
import com.carrental.dto.UserAnalyticsResponse;
import com.carrental.dto.VehiclePerformanceResponse;
import com.carrental.dto.VehicleResponse;
import com.carrental.entity.Booking;
import com.carrental.entity.Payment;
import com.carrental.entity.Review;
import com.carrental.entity.User;
import com.carrental.entity.Vehicle;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.carrental.enums.BookingStatus;
import com.carrental.enums.PaymentStatus;
import com.carrental.enums.ReviewStatus;
import com.carrental.enums.UserRole;
import com.carrental.enums.UserStatus;
import com.carrental.enums.VehicleStatus;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.PaymentRepository;
import com.carrental.repository.ReviewRepository;
import com.carrental.repository.UserRepository;
import com.carrental.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final UserRepository userRepository;
	private final VehicleRepository vehicleRepository;
	private final BookingRepository bookingRepository;
	private final PaymentRepository paymentRepository;
	private final ReviewRepository reviewRepository;

	@Override
	public AdminStatsResponse getAdminStats() {
		AdminStatsResponse stats = new AdminStatsResponse();

		// Total users
		stats.setTotalUsers(userRepository.count());

		// Total vehicles
		stats.setTotalVehicles(vehicleRepository.count());

		// Total bookings
		stats.setTotalBookings(bookingRepository.count());

		// Total revenue (from completed payments)
		List<Payment> completedPayments = paymentRepository.findAll().stream()
				.filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
				.collect(Collectors.toList());
		Double totalRevenue = completedPayments.stream()
				.mapToDouble(Payment::getAmount)
				.sum();
		stats.setTotalRevenue(totalRevenue);

		// Pending bookings
		stats.setPendingBookings((long) bookingRepository.findByStatus(BookingStatus.PENDING).size());

		// Completed bookings
		stats.setCompletedBookings((long) bookingRepository.findByStatus(BookingStatus.COMPLETED).size());

		// Active vehicles (AVAILABLE)
		stats.setActiveVehicles((long) vehicleRepository.findByStatus(VehicleStatus.AVAILABLE).size());

		// Booked vehicles
		stats.setBookedVehicles((long) vehicleRepository.findByStatus(VehicleStatus.BOOKED).size());

		return stats;
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public List<User> getPendingUsers() {
		return userRepository.findByStatus(UserStatus.PENDING);
	}

	@Override
	public List<BookingResponse> getAllBookings() {
		List<Booking> bookings = bookingRepository.findAll();
		return bookings.stream()
				.map(this::convertBookingToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<PaymentResponse> getAllPayments() {
		List<Payment> payments = paymentRepository.findAll();
		return payments.stream()
				.map(this::convertPaymentToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<VehicleResponse> getAllVehicles() {
		List<Vehicle> vehicles = vehicleRepository.findAll();
		return vehicles.stream()
				.map(this::convertVehicleToResponse)
				.collect(Collectors.toList());
	}

	// Helper method to convert Booking to BookingResponse
	private BookingResponse convertBookingToResponse(Booking booking) {
		BookingResponse response = new BookingResponse();
		response.setId(booking.getId());
		response.setUserId(booking.getUser().getId());
		response.setUserName(booking.getUser().getName());
		response.setUserEmail(booking.getUser().getEmail());
		response.setVehicleId(booking.getVehicle().getId());
		response.setVehicleMake(booking.getVehicle().getMake());
		response.setVehicleModel(booking.getVehicle().getModel());
		response.setVehicleYear(booking.getVehicle().getYear());
		response.setVehicleLicensePlate(booking.getVehicle().getLicensePlate());
		response.setVehiclePricePerDay(booking.getVehicle().getPricePerDay());
		response.setPickupDate(booking.getPickupDate());
		response.setReturnDate(booking.getReturnDate());
		response.setPickupLocation(booking.getPickupLocation());
		response.setReturnLocation(booking.getReturnLocation());
		response.setTotalAmount(booking.getTotalAmount());
		response.setStatus(booking.getStatus());
		response.setCreatedAt(booking.getCreatedAt());
		response.setUpdatedAt(booking.getUpdatedAt());
		response.setVendorId(booking.getVehicle().getVendor().getId());
		response.setVendorName(booking.getVehicle().getVendor().getName());
		return response;
	}

	// Helper method to convert Payment to PaymentResponse
	private PaymentResponse convertPaymentToResponse(Payment payment) {
		PaymentResponse response = new PaymentResponse();
		response.setId(payment.getId());
		response.setBookingId(payment.getBooking().getId());
		response.setAmount(payment.getAmount());
		response.setPaymentMethod(payment.getPaymentMethod());
		response.setStatus(payment.getStatus());
		response.setTransactionId(payment.getTransactionId());
		response.setPaymentDate(payment.getPaymentDate());
		response.setCreatedAt(payment.getCreatedAt());
		response.setUpdatedAt(payment.getUpdatedAt());
		response.setUserId(payment.getBooking().getUser().getId());
		response.setUserName(payment.getBooking().getUser().getName());
		response.setVehicleId(payment.getBooking().getVehicle().getId());
		response.setVehicleMake(payment.getBooking().getVehicle().getMake());
		response.setVehicleModel(payment.getBooking().getVehicle().getModel());
		return response;
	}

	// Helper method to convert Vehicle to VehicleResponse
	private VehicleResponse convertVehicleToResponse(Vehicle vehicle) {
		VehicleResponse response = new VehicleResponse();
		response.setId(vehicle.getId());
		response.setMake(vehicle.getMake());
		response.setModel(vehicle.getModel());
		response.setYear(vehicle.getYear());
		response.setColor(vehicle.getColor());
		response.setLicensePlate(vehicle.getLicensePlate());
		response.setVin(vehicle.getVin());
		response.setPricePerDay(vehicle.getPricePerDay());
		response.setStatus(vehicle.getStatus());
		response.setFuelType(vehicle.getFuelType());
		response.setTransmission(vehicle.getTransmission());
		response.setSeatingCapacity(vehicle.getSeatingCapacity());
		response.setDescription(vehicle.getDescription());
		response.setImageUrl(vehicle.getImageUrl());
		response.setVendorId(vehicle.getVendor().getId());
		response.setVendorName(vehicle.getVendor().getName());
		response.setCreatedAt(vehicle.getCreatedAt());
		response.setUpdatedAt(vehicle.getUpdatedAt());
		return response;
	}

	@Override
	public User approveUser(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.setStatus(UserStatus.APPROVED);
		return userRepository.save(user);
	}

	@Override
	public User rejectUser(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.setStatus(UserStatus.REJECTED);
		return userRepository.save(user);
	}

	@Override
	public void deleteUser(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		// Prevent deleting admin users
		if (user.getRole() == com.carrental.enums.UserRole.ADMIN) {
			throw new IllegalArgumentException("Cannot delete admin users");
		}
		
		// Check if user has associated vehicles (as vendor)
		List<Vehicle> userVehicles = vehicleRepository.findByVendorId(userId);
		if (!userVehicles.isEmpty()) {
			throw new IllegalArgumentException("Cannot delete user: User has " + userVehicles.size() + " vehicle(s) registered. Please delete or transfer vehicles first.");
		}
		
		// Check if user has bookings
		List<Booking> userBookings = bookingRepository.findByUserId(userId);
		if (!userBookings.isEmpty()) {
			throw new IllegalArgumentException("Cannot delete user: User has " + userBookings.size() + " booking(s). Please handle bookings first.");
		}
		
		userRepository.delete(user);
	}

	@Override
	public List<ReviewResponse> getAllReviews() {
		List<Review> reviews = reviewRepository.findAll();
		return reviews.stream()
				.map(this::convertReviewToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<ReviewResponse> getReviewsByStatus(ReviewStatus status) {
		List<Review> reviews = reviewRepository.findByStatus(status);
		return reviews.stream()
				.map(this::convertReviewToResponse)
				.collect(Collectors.toList());
	}

	private ReviewResponse convertReviewToResponse(Review review) {
		ReviewResponse response = new ReviewResponse();
		response.setId(review.getId());
		response.setUserId(review.getUser().getId());
		response.setUserName(review.getUser().getName());
		response.setUserEmail(review.getUser().getEmail());
		response.setVehicleId(review.getVehicle().getId());
		response.setVehicleMake(review.getVehicle().getMake());
		response.setVehicleModel(review.getVehicle().getModel());
		response.setRating(review.getRating());
		response.setComment(review.getComment());
		response.setStatus(review.getStatus());
		response.setCreatedAt(review.getCreatedAt());
		response.setUpdatedAt(review.getUpdatedAt());
		return response;
	}

	@Override
	public RevenueReportResponse getRevenueReport(String period) {
		RevenueReportResponse response = new RevenueReportResponse();
		List<Payment> allPayments = paymentRepository.findAll();
		List<Payment> completedPayments = allPayments.stream()
				.filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
				.collect(Collectors.toList());

		// Total revenue
		Double totalRevenue = completedPayments.stream()
				.mapToDouble(Payment::getAmount)
				.sum();
		response.setTotalRevenue(totalRevenue);

		// Monthly and yearly revenue
		LocalDate now = LocalDate.now();
		LocalDate startOfMonth = now.withDayOfMonth(1);
		LocalDate startOfYear = now.withDayOfYear(1);

		Double monthlyRevenue = completedPayments.stream()
				.filter(p -> p.getCreatedAt() != null && p.getCreatedAt().toLocalDate().isAfter(startOfMonth.minusDays(1)))
				.mapToDouble(Payment::getAmount)
				.sum();

		Double yearlyRevenue = completedPayments.stream()
				.filter(p -> p.getCreatedAt() != null && p.getCreatedAt().toLocalDate().isAfter(startOfYear.minusDays(1)))
				.mapToDouble(Payment::getAmount)
				.sum();

		response.setMonthlyRevenue(monthlyRevenue);
		response.setYearlyRevenue(yearlyRevenue);

		// Monthly breakdown (last 12 months)
		List<RevenueReportResponse.MonthlyRevenue> monthlyBreakdown = new ArrayList<>();
		for (int i = 11; i >= 0; i--) {
			YearMonth yearMonth = YearMonth.now().minusMonths(i);
			LocalDate monthStart = yearMonth.atDay(1);
			LocalDate monthEnd = yearMonth.atEndOfMonth();

			Double monthRevenue = completedPayments.stream()
					.filter(p -> {
						if (p.getCreatedAt() == null)
							return false;
						LocalDate paymentDate = p.getCreatedAt().toLocalDate();
						return !paymentDate.isBefore(monthStart) && !paymentDate.isAfter(monthEnd);
					})
					.mapToDouble(Payment::getAmount)
					.sum();

			long bookingCount = completedPayments.stream()
					.filter(p -> {
						if (p.getCreatedAt() == null)
							return false;
						LocalDate paymentDate = p.getCreatedAt().toLocalDate();
						return !paymentDate.isBefore(monthStart) && !paymentDate.isAfter(monthEnd);
					})
					.count();

			RevenueReportResponse.MonthlyRevenue monthly = new RevenueReportResponse.MonthlyRevenue();
			monthly.setMonth(yearMonth.getMonth().name());
			monthly.setYear(yearMonth.getYear());
			monthly.setRevenue(monthRevenue);
			monthly.setBookingCount((int) bookingCount);
			monthlyBreakdown.add(monthly);
		}
		response.setMonthlyBreakdown(monthlyBreakdown);

		// Revenue by status
		Map<String, Double> revenueByStatus = new HashMap<>();
		revenueByStatus.put("COMPLETED", completedPayments.stream().mapToDouble(Payment::getAmount).sum());
		revenueByStatus.put("PENDING",
				allPayments.stream().filter(p -> p.getStatus() == PaymentStatus.PENDING)
						.mapToDouble(Payment::getAmount).sum());
		revenueByStatus.put("FAILED",
				allPayments.stream().filter(p -> p.getStatus() == PaymentStatus.FAILED)
						.mapToDouble(Payment::getAmount).sum());
		response.setRevenueByStatus(revenueByStatus);

		// Average booking value
		if (!completedPayments.isEmpty()) {
			response.setAverageBookingValue(totalRevenue / completedPayments.size());
		} else {
			response.setAverageBookingValue(0.0);
		}

		response.setTotalTransactions(completedPayments.size());

		return response;
	}

	@Override
	public BookingAnalyticsResponse getBookingAnalytics() {
		BookingAnalyticsResponse response = new BookingAnalyticsResponse();
		List<Booking> allBookings = bookingRepository.findAll();

		response.setTotalBookings((long) allBookings.size());
		response.setPendingBookings((long) allBookings.stream().filter(b -> b.getStatus() == BookingStatus.PENDING)
				.count());
		response.setConfirmedBookings((long) allBookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
				.count());
		response.setCompletedBookings((long) allBookings.stream().filter(b -> b.getStatus() == BookingStatus.COMPLETED)
				.count());
		response.setCancelledBookings((long) allBookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED)
				.count());

		// Bookings by status
		Map<String, Long> bookingsByStatus = new HashMap<>();
		for (BookingStatus status : BookingStatus.values()) {
			bookingsByStatus.put(status.name(),
					(long) allBookings.stream().filter(b -> b.getStatus() == status).count());
		}
		response.setBookingsByStatus(bookingsByStatus);

		// Monthly bookings (last 12 months)
		List<BookingAnalyticsResponse.MonthlyBookings> monthlyBookings = new ArrayList<>();
		for (int i = 11; i >= 0; i--) {
			YearMonth yearMonth = YearMonth.now().minusMonths(i);
			LocalDate monthStart = yearMonth.atDay(1);
			LocalDate monthEnd = yearMonth.atEndOfMonth();

			long count = allBookings.stream()
					.filter(b -> {
						if (b.getCreatedAt() == null)
							return false;
						LocalDate bookingDate = b.getCreatedAt().toLocalDate();
						return !bookingDate.isBefore(monthStart) && !bookingDate.isAfter(monthEnd);
					})
					.count();

			BookingAnalyticsResponse.MonthlyBookings monthly = new BookingAnalyticsResponse.MonthlyBookings();
			monthly.setMonth(yearMonth.getMonth().name());
			monthly.setYear(yearMonth.getYear());
			monthly.setCount(count);
			monthlyBookings.add(monthly);
		}
		response.setMonthlyBookings(monthlyBookings);

		// Average booking duration
		if (!allBookings.isEmpty()) {
			double totalDays = allBookings.stream()
					.mapToLong(b -> java.time.temporal.ChronoUnit.DAYS.between(b.getPickupDate(), b.getReturnDate()))
					.sum();
			response.setAverageBookingDuration(totalDays / allBookings.size());
		} else {
			response.setAverageBookingDuration(0.0);
		}

		// Cancellation rate
		if (!allBookings.isEmpty()) {
			long cancelled = response.getCancelledBookings();
			response.setCancellationRate((cancelled * 100.0) / allBookings.size());
		} else {
			response.setCancellationRate(0.0);
		}

		return response;
	}

	@Override
	public VehiclePerformanceResponse getVehiclePerformance() {
		VehiclePerformanceResponse response = new VehiclePerformanceResponse();
		List<Vehicle> allVehicles = vehicleRepository.findAll();
		List<Booking> allBookings = bookingRepository.findAll();
		List<Payment> completedPayments = paymentRepository.findAll().stream()
				.filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
				.collect(Collectors.toList());
		List<Review> approvedReviews = reviewRepository.findByStatus(ReviewStatus.APPROVED);

		response.setTotalVehicles((long) allVehicles.size());
		response.setAvailableVehicles((long) allVehicles.stream()
				.filter(v -> v.getStatus() == VehicleStatus.AVAILABLE).count());
		response.setBookedVehicles((long) allVehicles.stream().filter(v -> v.getStatus() == VehicleStatus.BOOKED)
				.count());
		response.setMaintenanceVehicles((long) allVehicles.stream()
				.filter(v -> v.getStatus() == VehicleStatus.UNDER_MAINTENANCE).count());

		// Top vehicles by bookings and revenue
		Map<Integer, Long> vehicleBookingCounts = allBookings.stream()
				.collect(Collectors.groupingBy(b -> b.getVehicle().getId(), Collectors.counting()));

		Map<Integer, Double> vehicleRevenue = new HashMap<>();
		for (Payment payment : completedPayments) {
			if (payment.getBooking() != null && payment.getBooking().getVehicle() != null) {
				Integer vehicleId = payment.getBooking().getVehicle().getId();
				vehicleRevenue.put(vehicleId, vehicleRevenue.getOrDefault(vehicleId, 0.0) + payment.getAmount());
			}
		}

		Map<Integer, Double> vehicleRatings = new HashMap<>();
		Map<Integer, Long> vehicleRatingCounts = new HashMap<>();
		for (Review review : approvedReviews) {
			if (review.getVehicle() != null) {
				Integer vehicleId = review.getVehicle().getId();
				vehicleRatings.put(vehicleId, vehicleRatings.getOrDefault(vehicleId, 0.0) + review.getRating());
				vehicleRatingCounts.put(vehicleId, vehicleRatingCounts.getOrDefault(vehicleId, 0L) + 1);
			}
		}

		List<VehiclePerformanceResponse.TopVehicle> topVehicles = new ArrayList<>();
		for (Vehicle vehicle : allVehicles) {
			VehiclePerformanceResponse.TopVehicle topVehicle = new VehiclePerformanceResponse.TopVehicle();
			topVehicle.setVehicleId(vehicle.getId());
			topVehicle.setMake(vehicle.getMake());
			topVehicle.setModel(vehicle.getModel());
			topVehicle.setBookingCount(vehicleBookingCounts.getOrDefault(vehicle.getId(), 0L));
			topVehicle.setTotalRevenue(vehicleRevenue.getOrDefault(vehicle.getId(), 0.0));

			Long ratingCount = vehicleRatingCounts.getOrDefault(vehicle.getId(), 0L);
			if (ratingCount > 0) {
				topVehicle.setAverageRating(vehicleRatings.get(vehicle.getId()) / ratingCount);
			} else {
				topVehicle.setAverageRating(0.0);
			}

			topVehicles.add(topVehicle);
		}

		// Sort by revenue descending and take top 10
		topVehicles.sort((a, b) -> Double.compare(b.getTotalRevenue(), a.getTotalRevenue()));
		response.setTopVehicles(topVehicles.stream().limit(10).collect(Collectors.toList()));

		// Average utilization rate
		if (!allVehicles.isEmpty() && !allBookings.isEmpty()) {
			long totalBookingDays = allBookings.stream()
					.mapToLong(b -> java.time.temporal.ChronoUnit.DAYS.between(b.getPickupDate(), b.getReturnDate()))
					.sum();
			// Simplified: assume 365 days period
			double utilizationRate = (totalBookingDays * 100.0) / (allVehicles.size() * 365.0);
			response.setAverageUtilizationRate(Math.min(utilizationRate, 100.0));
		} else {
			response.setAverageUtilizationRate(0.0);
		}

		return response;
	}

	@Override
	public UserAnalyticsResponse getUserAnalytics() {
		UserAnalyticsResponse response = new UserAnalyticsResponse();
		List<User> allUsers = userRepository.findAll();
		List<Booking> allBookings = bookingRepository.findAll();
		List<Payment> completedPayments = paymentRepository.findAll().stream()
				.filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
				.collect(Collectors.toList());

		response.setTotalUsers((long) allUsers.size());

		// Active users (users with at least one booking)
		long activeUsers = allBookings.stream()
				.map(b -> b.getUser().getId())
				.distinct()
				.count();
		response.setActiveUsers(activeUsers);

		// New users this month
		LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
		// Note: User entity doesn't have createdAt field, so we'll use 0 for now
		// In a production system, you'd want to add createdAt to User entity
		long newUsersThisMonth = 0L;
		response.setNewUsersThisMonth(newUsersThisMonth);

		// Users by role
		Map<String, Long> usersByRole = new HashMap<>();
		for (UserRole role : UserRole.values()) {
			usersByRole.put(role.name(),
					(long) allUsers.stream().filter(u -> u.getRole() == role).count());
		}
		response.setUsersByRole(usersByRole);

		// Top customers
		Map<Integer, Long> userBookingCounts = allBookings.stream()
				.collect(Collectors.groupingBy(b -> b.getUser().getId(), Collectors.counting()));

		Map<Integer, Double> userSpending = new HashMap<>();
		for (Payment payment : completedPayments) {
			if (payment.getBooking() != null && payment.getBooking().getUser() != null) {
				Integer userId = payment.getBooking().getUser().getId();
				userSpending.put(userId, userSpending.getOrDefault(userId, 0.0) + payment.getAmount());
			}
		}

		List<UserAnalyticsResponse.TopCustomer> topCustomers = new ArrayList<>();
		for (User user : allUsers) {
			if (user.getRole() == UserRole.CUSTOMER) {
				UserAnalyticsResponse.TopCustomer topCustomer = new UserAnalyticsResponse.TopCustomer();
				topCustomer.setUserId(user.getId());
				topCustomer.setUserName(user.getName());
				topCustomer.setEmail(user.getEmail());
				topCustomer.setBookingCount(userBookingCounts.getOrDefault(user.getId(), 0L));
				topCustomer.setTotalSpent(userSpending.getOrDefault(user.getId(), 0.0));
				topCustomers.add(topCustomer);
			}
		}

		// Sort by total spent descending and take top 10
		topCustomers.sort((a, b) -> Double.compare(b.getTotalSpent(), a.getTotalSpent()));
		response.setTopCustomers(topCustomers.stream().limit(10).collect(Collectors.toList()));

		// Average bookings per user
		if (!allUsers.isEmpty() && !allBookings.isEmpty()) {
			response.setAverageBookingsPerUser((double) allBookings.size() / allUsers.size());
		} else {
			response.setAverageBookingsPerUser(0.0);
		}

		return response;
	}

}
