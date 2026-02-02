package com.carrental.controller;

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

import com.carrental.dto.PaymentRequest;
import com.carrental.dto.PaymentResponse;
import com.carrental.dto.PaymentStatusUpdateRequest;
import com.carrental.entity.User;
import com.carrental.enums.UserRole;
import com.carrental.repository.UserRepository;
import com.carrental.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {

	private final PaymentService paymentService;
	private final UserRepository userRepository;

	@PostMapping
	public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentRequest request) {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername(); // email is the username

			// Create payment
			PaymentResponse response = paymentService.createPayment(userEmail, request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<?> getPaymentByBookingId(@PathVariable Integer bookingId) {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername(); // email is the username

			// Get payment
			PaymentResponse response = paymentService.getPaymentByBookingId(userEmail, bookingId);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<?> updatePaymentStatus(@PathVariable Integer id,
			@Valid @RequestBody PaymentStatusUpdateRequest request) {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			// Verify admin role
			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can update payment status");
			}

			// Update payment status
			PaymentResponse response = paymentService.updatePaymentStatus(id, request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

}
