package com.carrental.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrental.dto.BookingRequest;
import com.carrental.dto.BookingResponse;
import com.carrental.entity.Booking;
import com.carrental.entity.User;
import com.carrental.entity.Vehicle;
import com.carrental.enums.BookingStatus;
import com.carrental.enums.VehicleStatus;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.UserRepository;
import com.carrental.repository.VehicleRepository;
import com.carrental.client.AuditClient;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;
	private final VehicleRepository vehicleRepository;
	private final AuditClient auditClient;

	@Override
	@Transactional
	public BookingResponse createBooking(String userEmail, BookingRequest request) {
		log.info("Initiating booking creation for user: {} on vehicle: {}", userEmail, request.getVehicleId());
		// Find user
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Find vehicle
		Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
				.orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

		// Validate vehicle is available
		if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
			log.warn("Booking failed: Vehicle {} is not AVAILABLE (Status: {})", vehicle.getId(), vehicle.getStatus());
			throw new IllegalArgumentException("Vehicle is not available for booking");
		}

		// Validate dates
		if (request.getReturnDate().isBefore(request.getPickupDate()) ||
				request.getReturnDate().isEqual(request.getPickupDate())) {
			throw new IllegalArgumentException("Return date must be after pickup date");
		}

		// Check for conflicting bookings
		List<BookingStatus> activeStatuses = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED,
				BookingStatus.ACTIVE);
		List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
				request.getVehicleId(),
				request.getPickupDate(),
				request.getReturnDate(),
				activeStatuses);

		if (!conflictingBookings.isEmpty()) {
			log.warn("Booking conflict detected for Vehicle {} between {} and {}", request.getVehicleId(),
					request.getPickupDate(), request.getReturnDate());
			throw new IllegalArgumentException("Vehicle is already booked for the selected dates");
		}

		// Calculate total amount
		long days = ChronoUnit.DAYS.between(request.getPickupDate(), request.getReturnDate());
		if (days <= 0) {
			days = 1; // Minimum 1 day
		}
		Double totalAmount = vehicle.getPricePerDay() * days;

		// Create booking
		Booking booking = new Booking();
		booking.setUser(user);
		booking.setVehicle(vehicle);
		booking.setPickupDate(request.getPickupDate());
		booking.setReturnDate(request.getReturnDate());
		booking.setPickupLocation(request.getPickupLocation());
		booking.setReturnLocation(request.getReturnLocation());
		booking.setTotalAmount(totalAmount);
		booking.setStatus(BookingStatus.PENDING);

		// Update vehicle status to BOOKED
		vehicle.setStatus(VehicleStatus.BOOKED);
		vehicleRepository.save(vehicle);

		// Save booking
		Booking savedBooking = bookingRepository.save(booking);
		log.info("Booking created successfully: ID={}, TotalAmount={}", savedBooking.getId(), totalAmount);

		// Audit Log
		auditClient.logActivity("BOOKING_CREATED", userEmail, "Booking ID: " + savedBooking.getId());

		// Convert to response
		return convertToResponse(savedBooking);
	}

	@Override
	public BookingResponse getBookingById(String userEmail, Integer bookingId) {
		// Find user
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Find booking
		Booking booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
				.orElseThrow(() -> new IllegalArgumentException("Booking not found"));

		return convertToResponse(booking);
	}

	@Override
	public List<BookingResponse> getUserBookings(String userEmail) {
		// Find user
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Get all bookings for user
		List<Booking> bookings = bookingRepository.findByUserId(user.getId());

		return bookings.stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public BookingResponse cancelBooking(String userEmail, Integer bookingId) {
		log.info("Cancellation requested for Booking ID: {} by User: {}", bookingId, userEmail);
		// Find user
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Find booking
		Booking booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
				.orElseThrow(() -> new IllegalArgumentException("Booking not found"));

		// Check if booking can be cancelled
		if (booking.getStatus() == BookingStatus.CANCELLED) {
			throw new IllegalArgumentException("Booking is already cancelled");
		}

		if (booking.getStatus() == BookingStatus.COMPLETED) {
			throw new IllegalArgumentException("Cannot cancel a completed booking");
		}

		// Store current status before updating
		BookingStatus currentStatus = booking.getStatus();

		// Update booking status
		booking.setStatus(BookingStatus.CANCELLED);

		// Update vehicle status back to AVAILABLE if booking was confirmed or active
		if (currentStatus == BookingStatus.CONFIRMED || currentStatus == BookingStatus.ACTIVE) {
			Vehicle vehicle = booking.getVehicle();
			vehicle.setStatus(VehicleStatus.AVAILABLE);
			vehicleRepository.save(vehicle);
		}

		// Save booking
		bookingRepository.save(booking);

		// Audit Log
		auditClient.logActivity("BOOKING_CANCELLED", userEmail, "Booking ID: " + bookingId);

		return convertToResponse(booking);
	}

	@Override
	public List<BookingResponse> getVendorBookings(String vendorEmail) {
		// Find vendor
		User vendor = userRepository.findByEmail(vendorEmail)
				.orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

		// Get all bookings for vendor's vehicles
		List<Booking> bookings = bookingRepository.findByVendorId(vendor.getId());

		return bookings.stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	// Helper method to convert Booking entity to BookingResponse DTO
	private BookingResponse convertToResponse(Booking booking) {
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
		response.setVehicleImageUrl(booking.getVehicle().getImageUrl());
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

}
