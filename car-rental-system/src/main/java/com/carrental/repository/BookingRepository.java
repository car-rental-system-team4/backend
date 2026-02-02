package com.carrental.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carrental.entity.Booking;
import com.carrental.enums.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

	List<Booking> findByUserId(Integer userId);

	List<Booking> findByVehicleId(Integer vehicleId);

	List<Booking> findByStatus(BookingStatus status);

	Optional<Booking> findByIdAndUserId(Integer id, Integer userId);

	@Query("SELECT b FROM Booking b WHERE b.vehicle.vendor.id = :vendorId")
	List<Booking> findByVendorId(@Param("vendorId") Integer vendorId);

	@Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId AND b.status IN :statuses AND " +
			"((b.pickupDate <= :returnDate AND b.returnDate >= :pickupDate))")
	List<Booking> findConflictingBookings(@Param("vehicleId") Integer vehicleId,
			@Param("pickupDate") LocalDate pickupDate, @Param("returnDate") LocalDate returnDate,
			@Param("statuses") List<BookingStatus> statuses);

}
