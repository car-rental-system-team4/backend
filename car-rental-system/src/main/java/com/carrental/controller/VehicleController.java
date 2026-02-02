package com.carrental.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrental.dto.VehicleRequest;
import com.carrental.dto.VehicleResponse;
import com.carrental.dto.VehicleStatusUpdateRequest;
import com.carrental.service.VehicleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VehicleController {

	private final VehicleService vehicleService;
	private final com.carrental.service.FileStorageService fileStorageService;

	@PostMapping(consumes = { "multipart/form-data" })
	public ResponseEntity<?> addVehicle(@Valid @ModelAttribute VehicleRequest request) {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			// Handle Image Upload
			if (request.getImage() != null && !request.getImage().isEmpty()) {
				String imageUrl = fileStorageService.saveFile(request.getImage());
				request.setImageUrl(imageUrl);
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String vendorEmail = userDetails.getUsername(); // email is the username

			// Add vehicle
			VehicleResponse response = vehicleService.addVehicle(vendorEmail, request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
	public ResponseEntity<?> updateVehicle(@PathVariable Integer id,
			@Valid @ModelAttribute VehicleRequest request) {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			// Handle Image Upload if a new file is provided
			if (request.getImage() != null && !request.getImage().isEmpty()) {
				String imageUrl = fileStorageService.saveFile(request.getImage());
				request.setImageUrl(imageUrl);
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String vendorEmail = userDetails.getUsername(); // email is the username

			// Update vehicle
			VehicleResponse response = vehicleService.updateVehicle(vendorEmail, id, request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteVehicle(@PathVariable Integer id) {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String vendorEmail = userDetails.getUsername(); // email is the username

			// Delete vehicle
			boolean success = vehicleService.deleteVehicle(vendorEmail, id);
			if (success) {
				return ResponseEntity.ok("Vehicle deleted successfully");
			} else {
				return ResponseEntity.badRequest().body("Failed to delete vehicle");
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> getAllAvailableVehicles() {
		try {
			List<VehicleResponse> vehicles = vehicleService.getAllAvailableVehicles();
			return ResponseEntity.ok(vehicles);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getVehicleById(@PathVariable Integer id) {
		try {
			VehicleResponse vehicle = vehicleService.getVehicleById(id);
			return ResponseEntity.ok(vehicle);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@GetMapping("/vendor")
	public ResponseEntity<?> getVendorVehicles() {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String vendorEmail = userDetails.getUsername(); // email is the username

			// Get vendor vehicles
			List<VehicleResponse> vehicles = vehicleService.getVendorVehicles(vendorEmail);
			return ResponseEntity.ok(vehicles);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<?> updateVehicleStatus(@PathVariable Integer id,
			@Valid @RequestBody VehicleStatusUpdateRequest request) {
		try {
			// Extract authenticated user from SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String vendorEmail = userDetails.getUsername(); // email is the username

			// Update vehicle status
			VehicleResponse response = vehicleService.updateVehicleStatus(vendorEmail, id, request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}

}
