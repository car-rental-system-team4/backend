package com.carrental.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrental.dto.BookingRequest;
import com.carrental.dto.BookingResponse;
import com.carrental.entity.User;
import com.carrental.enums.UserRole;
import com.carrental.repository.UserRepository;
import com.carrental.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BookingController {

	private final BookingService bookingService;
	private final UserRepository userRepository;

	@PostMapping
	public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest request) {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername(); // email is the username

			// Create booking
			BookingResponse response = bookingService.createBooking(userEmail, request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getBookingById(@PathVariable Integer id) {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername(); // email is the username

			// Get booking
			BookingResponse response = bookingService.getBookingById(userEmail, id);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/user")
	public ResponseEntity<?> getUserBookings() {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername(); // email is the username

			// Get user bookings
			List<BookingResponse> bookings = bookingService.getUserBookings(userEmail);
			return ResponseEntity.ok(bookings);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@PutMapping("/{id}/cancel")
	public ResponseEntity<?> cancelBooking(@PathVariable Integer id) {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername(); // email is the username

			// Cancel booking
			BookingResponse response = bookingService.cancelBooking(userEmail, id);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/vendor")
	public ResponseEntity<?> getVendorBookings() {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String vendorEmail = userDetails.getUsername(); // email is the username

			// Verify vendor role
			User vendor = userRepository.findByEmail(vendorEmail)
					.orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
			
			if (vendor.getRole() != UserRole.VENDOR) {
				return ResponseEntity.status(403).body("Only vendors can access this endpoint");
			}

			// Get vendor bookings
			List<BookingResponse> bookings = bookingService.getVendorBookings(vendorEmail);
			return ResponseEntity.ok(bookings);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

}
