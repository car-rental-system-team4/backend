package com.carrental.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrental.entity.Vehicle;
import com.carrental.enums.VehicleStatus;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

	List<Vehicle> findByVendorId(Integer vendorId);

	List<Vehicle> findByStatus(VehicleStatus status);

	Optional<Vehicle> findByLicensePlate(String licensePlate);

	Optional<Vehicle> findByVin(String vin);

	List<Vehicle> findByVendorIdAndStatus(Integer vendorId, VehicleStatus status);

}
