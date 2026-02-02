package com.carrental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrental.entity.Review;
import com.carrental.enums.ReviewStatus;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	List<Review> findByVehicleId(Integer vehicleId);

	List<Review> findByUserId(Integer userId);

	List<Review> findByStatus(ReviewStatus status);

	List<Review> findByVehicleIdAndStatus(Integer vehicleId, ReviewStatus status);

	List<Review> findByUserIdAndVehicleId(Integer userId, Integer vehicleId);

}
