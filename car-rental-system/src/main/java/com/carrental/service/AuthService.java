package com.carrental.service;

import com.carrental.dto.JwtAuthenticationResponse;
import com.carrental.dto.LoginRequest;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UpdateProfileRequest;

public interface AuthService {

	// Method to register a new user
	boolean register(RegisterRequest request);

	// Method to login and get Token
	JwtAuthenticationResponse login(LoginRequest request);

	// Helper to check if email exists
	boolean hasUserWithEmail(String email);

	// Method to update user profile
	boolean updateProfile(String userEmail, UpdateProfileRequest request);

	// Method to delete user profile
	boolean deleteProfile(String userEmail);

	// Method to get user profile
	com.carrental.dto.JwtAuthenticationResponse getProfile(String userEmail);
}