package com.carrental.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrental.dto.AdminStatsResponse;
import com.carrental.dto.BookingResponse;
import com.carrental.dto.PaymentResponse;
import com.carrental.dto.VehicleResponse;
import com.carrental.entity.User;
import com.carrental.enums.UserRole;
import com.carrental.repository.UserRepository;
import com.carrental.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminController {

	private final AdminService adminService;
	private final UserRepository userRepository;

	@GetMapping("/stats")
	public ResponseEntity<?> getAdminStats() {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			AdminStatsResponse stats = adminService.getAdminStats();
			return ResponseEntity.ok(stats);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			List<User> users = adminService.getAllUsers();
			return ResponseEntity.ok(users);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/bookings")
	public ResponseEntity<?> getAllBookings() {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			List<BookingResponse> bookings = adminService.getAllBookings();
			return ResponseEntity.ok(bookings);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/payments")
	public ResponseEntity<?> getAllPayments() {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			List<PaymentResponse> payments = adminService.getAllPayments();
			return ResponseEntity.ok(payments);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/vehicles")
	public ResponseEntity<?> getAllVehicles() {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			List<VehicleResponse> vehicles = adminService.getAllVehicles();
			return ResponseEntity.ok(vehicles);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/users/pending")
	public ResponseEntity<?> getPendingUsers() {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			List<User> pendingUsers = adminService.getPendingUsers();
			return ResponseEntity.ok(pendingUsers);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@PutMapping("/users/{userId}/approve")
	public ResponseEntity<?> approveUser(@PathVariable Integer userId) {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User admin = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (admin.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			User approvedUser = adminService.approveUser(userId);
			return ResponseEntity.ok(approvedUser);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@PutMapping("/users/{userId}/reject")
	public ResponseEntity<?> rejectUser(@PathVariable Integer userId) {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User admin = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (admin.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			User rejectedUser = adminService.rejectUser(userId);
			return ResponseEntity.ok(rejectedUser);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@DeleteMapping("/users/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User admin = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (admin.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			// Prevent admin from deleting themselves
			if (admin.getId().equals(userId)) {
				return ResponseEntity.badRequest().body("Cannot delete your own account");
			}

			adminService.deleteUser(userId);
			return ResponseEntity.ok("User deleted successfully");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/reviews")
	public ResponseEntity<?> getAllReviews() {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			List<com.carrental.dto.ReviewResponse> reviews = adminService.getAllReviews();
			return ResponseEntity.ok(reviews);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/reviews/status/{status}")
	public ResponseEntity<?> getReviewsByStatus(@PathVariable String status) {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			com.carrental.enums.ReviewStatus reviewStatus;
			try {
				reviewStatus = com.carrental.enums.ReviewStatus.valueOf(status.toUpperCase());
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest().body("Invalid status: " + status);
			}

			List<com.carrental.dto.ReviewResponse> reviews = adminService.getReviewsByStatus(reviewStatus);
			return ResponseEntity.ok(reviews);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/reports/revenue")
	public ResponseEntity<?> getRevenueReport(@RequestParam(required = false, defaultValue = "year") String period) {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			com.carrental.dto.RevenueReportResponse report = adminService.getRevenueReport(period);
			return ResponseEntity.ok(report);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/reports/bookings")
	public ResponseEntity<?> getBookingAnalytics() {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			com.carrental.dto.BookingAnalyticsResponse analytics = adminService.getBookingAnalytics();
			return ResponseEntity.ok(analytics);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/reports/vehicles")
	public ResponseEntity<?> getVehiclePerformance() {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			com.carrental.dto.VehiclePerformanceResponse performance = adminService.getVehiclePerformance();
			return ResponseEntity.ok(performance);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/reports/users")
	public ResponseEntity<?> getUserAnalytics() {
		try {
			// Verify admin role
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			if (user.getRole() != UserRole.ADMIN) {
				return ResponseEntity.status(403).body("Only admins can access this endpoint");
			}

			com.carrental.dto.UserAnalyticsResponse analytics = adminService.getUserAnalytics();
			return ResponseEntity.ok(analytics);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

}
