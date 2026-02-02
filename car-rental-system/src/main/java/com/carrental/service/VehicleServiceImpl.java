package com.carrental.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.carrental.dto.VehicleRequest;
import com.carrental.dto.VehicleResponse;
import com.carrental.dto.VehicleStatusUpdateRequest;
import com.carrental.entity.User;
import com.carrental.entity.Vehicle;
import com.carrental.enums.UserRole;
import com.carrental.enums.VehicleStatus;
import com.carrental.repository.UserRepository;
import com.carrental.repository.VehicleRepository;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceImpl implements VehicleService {

	private final VehicleRepository vehicleRepository;
	private final UserRepository userRepository;
	private final com.carrental.service.FileStorageService fileStorageService;

	@Override
	public VehicleResponse addVehicle(String vendorEmail, VehicleRequest request) {
		log.info("Adding new vehicle for vendor: {}", vendorEmail);
		// Find vendor by email
		User vendor = userRepository.findByEmail(vendorEmail)
				.orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

		// Verify vendor role
		if (vendor.getRole() != UserRole.VENDOR) {
			log.error("Access denied: User {} tried to add vehicle but is not a VENDOR", vendorEmail);
			throw new IllegalArgumentException("Only vendors can add vehicles");
		}

		// Check for duplicate license plate
		if (vehicleRepository.findByLicensePlate(request.getLicensePlate()).isPresent()) {
			log.warn("Duplicate license plate attempt: {}", request.getLicensePlate());
			throw new IllegalArgumentException("License plate already exists");
		}

		// Check for duplicate VIN
		if (vehicleRepository.findByVin(request.getVin()).isPresent()) {
			log.warn("Duplicate VIN attempt: {}", request.getVin());
			throw new IllegalArgumentException("VIN already exists");
		}

		// Create vehicle entity
		Vehicle vehicle = new Vehicle();
		vehicle.setMake(request.getMake());
		vehicle.setManufacturer(request.getMake()); // Map make to manufacturer for database
		vehicle.setModel(request.getModel());
		vehicle.setYear(request.getYear());
		vehicle.setColor(request.getColor());
		vehicle.setLicensePlate(request.getLicensePlate());
		vehicle.setVin(request.getVin());
		vehicle.setPricePerDay(request.getPricePerDay());
		vehicle.setFuelType(request.getFuelType());
		vehicle.setTransmission(request.getTransmission());
		vehicle.setSeatingCapacity(request.getSeatingCapacity());
		vehicle.setDescription(request.getDescription());
		vehicle.setImageUrl(request.getImageUrl());
		vehicle.setVendor(vendor);
		vehicle.setStatus(VehicleStatus.AVAILABLE);

		// Save vehicle
		Vehicle savedVehicle = vehicleRepository.save(vehicle);
		log.info("Vehicle added successfully: ID={}, License={}", savedVehicle.getId(), savedVehicle.getLicensePlate());

		// Convert to response DTO
		return convertToResponse(savedVehicle);
	}

	@Override
	public VehicleResponse updateVehicle(String vendorEmail, Integer vehicleId, VehicleRequest request) {
		log.info("Updating vehicle ID: {} for vendor: {}", vehicleId, vendorEmail);
		// Find vendor by email
		User vendor = userRepository.findByEmail(vendorEmail)
				.orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

		// Verify vendor role
		if (vendor.getRole() != UserRole.VENDOR) {
			throw new IllegalArgumentException("Only vendors can update vehicles");
		}

		// Find vehicle by ID
		Vehicle vehicle = vehicleRepository.findById(vehicleId)
				.orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

		// Verify ownership
		if (!vehicle.getVendor().getId().equals(vendor.getId())) {
			log.error("Unauthorized update attempt: Vendor {} tried to update Vehicle {}", vendor.getId(), vehicleId);
			throw new IllegalArgumentException("You can only update your own vehicles");
		}

		// Check for duplicate license plate (if changed)
		if (!vehicle.getLicensePlate().equals(request.getLicensePlate())) {
			Optional<Vehicle> existingVehicleWithLicense = vehicleRepository
					.findByLicensePlate(request.getLicensePlate());
			if (existingVehicleWithLicense.isPresent()
					&& !existingVehicleWithLicense.get().getId().equals(vehicle.getId())) {
				throw new IllegalArgumentException("License plate already exists");
			}
		}

		// Check for duplicate VIN (if changed)
		if (!vehicle.getVin().equals(request.getVin())) {
			Optional<Vehicle> existingVehicleWithVin = vehicleRepository.findByVin(request.getVin());
			if (existingVehicleWithVin.isPresent() && !existingVehicleWithVin.get().getId().equals(vehicle.getId())) {
				throw new IllegalArgumentException("VIN already exists");
			}
		}

		// Update vehicle fields
		vehicle.setMake(request.getMake());
		vehicle.setManufacturer(request.getMake()); // Map make to manufacturer for database
		vehicle.setModel(request.getModel());
		vehicle.setYear(request.getYear());
		vehicle.setColor(request.getColor());
		vehicle.setLicensePlate(request.getLicensePlate());
		vehicle.setVin(request.getVin());
		vehicle.setPricePerDay(request.getPricePerDay());
		vehicle.setFuelType(request.getFuelType());
		vehicle.setTransmission(request.getTransmission());
		vehicle.setSeatingCapacity(request.getSeatingCapacity());
		vehicle.setDescription(request.getDescription());
		vehicle.setImageUrl(request.getImageUrl());

		// Save updated vehicle
		Vehicle updatedVehicle = vehicleRepository.save(vehicle);
		log.info("Vehicle updated successfully: ID={}", updatedVehicle.getId());

		// Convert to response DTO
		return convertToResponse(updatedVehicle);
	}

	@Override
	public boolean deleteVehicle(String userEmail, Integer vehicleId) {
		log.info("Deleting vehicle ID: {} requested by: {}", vehicleId, userEmail);
		// Find user by email
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Verify user role (Vendor or Admin)
		if (user.getRole() != UserRole.VENDOR && user.getRole() != UserRole.ADMIN) {
			throw new IllegalArgumentException("Only vendors or admins can delete vehicles");
		}

		// Find vehicle by ID
		Vehicle vehicle = vehicleRepository.findById(vehicleId)
				.orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

		// Verify ownership (Skip check for Admin)
		if (user.getRole() == UserRole.VENDOR && !vehicle.getVendor().getId().equals(user.getId())) {
			log.error("Unauthorized delete attempt: Vendor {} on Vehicle {}", user.getId(), vehicleId);
			throw new IllegalArgumentException("You can only delete your own vehicles");
		}

		// Delete vehicle
		vehicleRepository.delete(vehicle);
		log.info("Vehicle deleted successfully: ID={}", vehicleId);
		return true;
	}

	@Override
	public List<VehicleResponse> getAllAvailableVehicles() {
		// Get all vehicles with AVAILABLE status
		List<Vehicle> vehicles = vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);

		// Convert to response DTOs
		return vehicles.stream().map(this::convertToResponse).collect(Collectors.toList());
	}

	@Override
	public VehicleResponse getVehicleById(Integer vehicleId) {
		// Find vehicle by ID
		Vehicle vehicle = vehicleRepository.findById(vehicleId)
				.orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

		// Convert to response DTO
		return convertToResponse(vehicle);
	}

	@Override
	public List<VehicleResponse> getVendorVehicles(String vendorEmail) {
		// Find vendor by email
		User vendor = userRepository.findByEmail(vendorEmail)
				.orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

		// Verify vendor role
		if (vendor.getRole() != UserRole.VENDOR) {
			throw new IllegalArgumentException("Only vendors can view their vehicles");
		}

		// Get all vehicles for this vendor
		List<Vehicle> vehicles = vehicleRepository.findByVendorId(vendor.getId());

		// Convert to response DTOs
		return vehicles.stream().map(this::convertToResponse).collect(Collectors.toList());
	}

	@Override
	public VehicleResponse updateVehicleStatus(String vendorEmail, Integer vehicleId,
			VehicleStatusUpdateRequest request) {
		log.info("Updating status for Vehicle ID: {} by Vendor: {}", vehicleId, vendorEmail);
		// Find vendor by email
		User vendor = userRepository.findByEmail(vendorEmail)
				.orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

		// Verify vendor role
		if (vendor.getRole() != UserRole.VENDOR) {
			throw new IllegalArgumentException("Only vendors can update vehicle status");
		}

		// Find vehicle by ID
		Vehicle vehicle = vehicleRepository.findById(vehicleId)
				.orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

		// Verify ownership
		if (!vehicle.getVendor().getId().equals(vendor.getId())) {
			throw new IllegalArgumentException("You can only update status of your own vehicles");
		}

		// Parse and validate status
		VehicleStatus newStatus;
		try {
			newStatus = VehicleStatus.valueOf(request.getStatus().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid vehicle status: " + request.getStatus());
		}

		// Update status
		vehicle.setStatus(newStatus);

		// Save updated vehicle
		Vehicle updatedVehicle = vehicleRepository.save(vehicle);
		log.info("Vehicle status updated to {} for ID={}", newStatus, vehicleId);

		// Convert to response DTO
		return convertToResponse(updatedVehicle);
	}

	// Helper method to convert Vehicle entity to VehicleResponse DTO
	private VehicleResponse convertToResponse(Vehicle vehicle) {
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

		// Generate presigned URL for the image
		if (vehicle.getImageUrl() != null && !vehicle.getImageUrl().isEmpty()) {
			response.setImageUrl(fileStorageService.generatePresignedUrl(vehicle.getImageUrl()));
		} else {
			response.setImageUrl(null);
		}

		response.setVendorId(vehicle.getVendor().getId());
		response.setVendorName(vehicle.getVendor().getName());
		response.setCreatedAt(vehicle.getCreatedAt());
		response.setUpdatedAt(vehicle.getUpdatedAt());
		return response;
	}

}
