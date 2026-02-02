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

import com.carrental.dto.ReviewRequest;
import com.carrental.dto.ReviewResponse;
import com.carrental.entity.User;
import com.carrental.enums.UserRole;
import com.carrental.repository.UserRepository;
import com.carrental.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReviewController {

	private final ReviewService reviewService;
	private final UserRepository userRepository;

	@PostMapping
	public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequest request) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			ReviewResponse review = reviewService.createReview(userEmail, request);
			return ResponseEntity.ok(review);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/vehicle/{vehicleId}")
	public ResponseEntity<?> getReviewsByVehicleId(@PathVariable Integer vehicleId) {
		try {
			List<ReviewResponse> reviews = reviewService.getReviewsByVehicleId(vehicleId);
			return ResponseEntity.ok(reviews);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@PutMapping("/{id}/approve")
	public ResponseEntity<?> approveReview(@PathVariable Integer id) {
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
				return ResponseEntity.status(403).body("Only admins can approve reviews");
			}

			ReviewResponse review = reviewService.approveReview(id);
			return ResponseEntity.ok(review);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@PutMapping("/{id}/reject")
	public ResponseEntity<?> rejectReview(@PathVariable Integer id) {
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
				return ResponseEntity.status(403).body("Only admins can reject reviews");
			}

			ReviewResponse review = reviewService.rejectReview(id);
			return ResponseEntity.ok(review);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

}
