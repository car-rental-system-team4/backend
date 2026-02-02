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

import com.carrental.dto.ComplaintRequest;
import com.carrental.dto.ComplaintResponse;
import com.carrental.dto.ComplaintResolutionRequest;
import com.carrental.entity.User;
import com.carrental.enums.UserRole;
import com.carrental.repository.UserRepository;
import com.carrental.service.ComplaintService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ComplaintController {

	private final ComplaintService complaintService;
	private final UserRepository userRepository;

	@PostMapping
	public ResponseEntity<?> createComplaint(@Valid @RequestBody ComplaintRequest request) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			ComplaintResponse complaint = complaintService.createComplaint(userEmail, request);
			return ResponseEntity.ok(complaint);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/user")
	public ResponseEntity<?> getUserComplaints() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername();

			List<ComplaintResponse> complaints = complaintService.getUserComplaints(userEmail);
			return ResponseEntity.ok(complaints);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> getAllComplaints() {
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

			List<ComplaintResponse> complaints = complaintService.getAllComplaints();
			return ResponseEntity.ok(complaints);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@PutMapping("/{id}/resolve")
	public ResponseEntity<?> resolveComplaint(@PathVariable Integer id,
			@Valid @RequestBody ComplaintResolutionRequest request) {
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
				return ResponseEntity.status(403).body("Only admins can resolve complaints");
			}

			ComplaintResponse complaint = complaintService.resolveComplaint(id, request);
			return ResponseEntity.ok(complaint);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

}
