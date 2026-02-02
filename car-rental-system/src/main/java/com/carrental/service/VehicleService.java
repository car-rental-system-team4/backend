package com.carrental.service;

import java.util.List;

import com.carrental.dto.VehicleRequest;
import com.carrental.dto.VehicleResponse;
import com.carrental.dto.VehicleStatusUpdateRequest;
import com.carrental.enums.VehicleStatus;

public interface VehicleService {

	// Add a new vehicle (Vendor only)
	VehicleResponse addVehicle(String vendorEmail, VehicleRequest request);

	// Update vehicle details (Vendor only, must own the vehicle)
	VehicleResponse updateVehicle(String vendorEmail, Integer vehicleId, VehicleRequest request);

	// Delete a vehicle (Vendor only, must own the vehicle)
	boolean deleteVehicle(String vendorEmail, Integer vehicleId);

	// Get all available vehicles (Public/User access)
	List<VehicleResponse> getAllAvailableVehicles();

	// Get vehicle by ID (Public/User access)
	VehicleResponse getVehicleById(Integer vehicleId);

	// Get all vehicles for a vendor (Vendor only)
	List<VehicleResponse> getVendorVehicles(String vendorEmail);

	// Update vehicle status (Vendor only, must own the vehicle)
	VehicleResponse updateVehicleStatus(String vendorEmail, Integer vehicleId, VehicleStatusUpdateRequest request);

}
