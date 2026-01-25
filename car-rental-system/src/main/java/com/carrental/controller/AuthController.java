package com.carrental.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrental.dto.LoginRequest;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UpdateProfileRequest;
import com.carrental.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
		boolean success = authService.register(req);
		if (!success) {
			return ResponseEntity.badRequest().body("Email already registered");
		}
		return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req) {
		try {
			return ResponseEntity.ok(authService.login(req));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Invalid Username or Password");
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

	@DeleteMapping("/profile")
	public ResponseEntity<?> deleteProfile() {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userEmail = userDetails.getUsername(); // email is the username

			// Delete profile
			boolean success = authService.deleteProfile(userEmail);
			if (success) {
				return ResponseEntity.ok("Profile deleted successfully");
			} else {
				return ResponseEntity.badRequest().body("Failed to delete profile");
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}
}
