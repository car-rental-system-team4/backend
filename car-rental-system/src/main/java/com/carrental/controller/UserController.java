package com.carrental.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrental.dto.JwtAuthenticationResponse;
import com.carrental.dto.UpdateProfileRequest;
import com.carrental.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

	private final AuthService authService;

	@GetMapping("/profile")
	public ResponseEntity<?> getProfile() {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername(); // email is the username

			// Get profile
			JwtAuthenticationResponse profile = authService.getProfile(userEmail);
			return ResponseEntity.ok(profile);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@PutMapping("/profile")
	public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername(); // email is the username

			// Update profile
			boolean success = authService.updateProfile(userEmail, request);
			if (success) {
				return ResponseEntity.ok("Profile updated successfully");
			} else {
				return ResponseEntity.badRequest().body("Failed to update profile");
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}
}
