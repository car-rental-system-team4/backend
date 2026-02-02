package com.carrental.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.carrental.dto.ReviewRequest;
import com.carrental.dto.ReviewResponse;
import com.carrental.entity.Review;
import com.carrental.entity.User;
import com.carrental.entity.Vehicle;
import com.carrental.enums.ReviewStatus;
import com.carrental.repository.ReviewRepository;
import com.carrental.repository.UserRepository;
import com.carrental.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final VehicleRepository vehicleRepository;

	@Override
	public ReviewResponse createReview(String userEmail, ReviewRequest request) {
		// Get user
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Get vehicle
		Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
				.orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

		// Check if user has already reviewed this vehicle
		List<Review> existingReviews = reviewRepository.findByUserIdAndVehicleId(user.getId(), request.getVehicleId());
		if (!existingReviews.isEmpty()) {
			throw new IllegalArgumentException("You have already reviewed this vehicle");
		}

		// Create review
		Review review = new Review();
		review.setUser(user);
		review.setVehicle(vehicle);
		review.setRating(request.getRating());
		review.setComment(request.getComment());
		review.setStatus(ReviewStatus.PENDING);

		Review savedReview = reviewRepository.save(review);
		return convertToResponse(savedReview);
	}

	@Override
	public List<ReviewResponse> getReviewsByVehicleId(Integer vehicleId) {
		// Only return approved reviews for public viewing
		List<Review> reviews = reviewRepository.findByVehicleIdAndStatus(vehicleId, ReviewStatus.APPROVED);
		return reviews.stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public ReviewResponse approveReview(Integer reviewId) {
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("Review not found"));

		review.setStatus(ReviewStatus.APPROVED);
		Review savedReview = reviewRepository.save(review);
		return convertToResponse(savedReview);
	}

	@Override
	public ReviewResponse rejectReview(Integer reviewId) {
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("Review not found"));

		review.setStatus(ReviewStatus.REJECTED);
		Review savedReview = reviewRepository.save(review);
		return convertToResponse(savedReview);
	}

	private ReviewResponse convertToResponse(Review review) {
		ReviewResponse response = new ReviewResponse();
		response.setId(review.getId());
		response.setUserId(review.getUser().getId());
		response.setUserName(review.getUser().getName());
		response.setUserEmail(review.getUser().getEmail());
		response.setVehicleId(review.getVehicle().getId());
		response.setVehicleMake(review.getVehicle().getMake());
		response.setVehicleModel(review.getVehicle().getModel());
		response.setRating(review.getRating());
		response.setComment(review.getComment());
		response.setStatus(review.getStatus());
		response.setCreatedAt(review.getCreatedAt());
		response.setUpdatedAt(review.getUpdatedAt());
		return response;
	}

}
