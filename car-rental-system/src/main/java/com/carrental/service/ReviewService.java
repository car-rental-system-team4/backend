package com.carrental.service;

import java.util.List;

import com.carrental.dto.ReviewRequest;
import com.carrental.dto.ReviewResponse;

public interface ReviewService {

	ReviewResponse createReview(String userEmail, ReviewRequest request);

	List<ReviewResponse> getReviewsByVehicleId(Integer vehicleId);

	ReviewResponse approveReview(Integer reviewId);

	ReviewResponse rejectReview(Integer reviewId);

}
