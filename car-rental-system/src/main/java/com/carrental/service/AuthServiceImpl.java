package com.carrental.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carrental.dto.JwtAuthenticationResponse;
import com.carrental.dto.LoginRequest;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UpdateProfileRequest;
import com.carrental.entity.User;
import com.carrental.enums.Gender;
import com.carrental.enums.UserRole;
import com.carrental.repository.UserRepository;
import com.carrental.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepo;
	private final PasswordEncoder encoder;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	@Override
	public boolean register(RegisterRequest req) {
		// Check if email exists
		if (userRepo.findByEmail(req.getEmail()).isPresent()) {
			return false;
		}
		// Create User Entity
		User user = new User();

		user.setName(req.getName());
		user.setEmail(req.getEmail());
		user.setPassword(encoder.encode(req.getPassword()));
		user.setPhoneNo(req.getPhoneNo());

		user.setLicenseNo(req.getLicenseNo());
		user.setAadharNo(req.getAadharNo());
		user.setHouseNo(req.getHouseNo());
		user.setBuildingName(req.getBuildingName());
		user.setStreetName(req.getStreetName());
		user.setArea(req.getArea());
		user.setPincode(req.getPincode());

		// Set Role
		if (req.getRole() != null) {
			try {
				user.setRole(UserRole.valueOf(req.getRole()));
			} catch (IllegalArgumentException e) {
				user.setRole(UserRole.CUSTOMER);
			}
		} else {
			user.setRole(UserRole.CUSTOMER);
		}

		// Set Gender
		if (req.getGender() != null) {
			try {
				user.setGender(Gender.valueOf(req.getGender()));
			} catch (IllegalArgumentException e) {

			}
		}
		userRepo.save(user);
		return true;
	}

	@Override
	public JwtAuthenticationResponse login(LoginRequest req) {
		// Authenticate with Spring Security
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

		User user = userRepo.findByEmail(req.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

		// Generate Token
		String token = jwtUtil.generateToken(user);

		// Return Token and Role
		JwtAuthenticationResponse response = new JwtAuthenticationResponse();
		response.setToken(token);
		response.setRole(user.getRole().name());
		response.setName(user.getName());
		response.setUserId(user.getId());
		response.setEmail(user.getEmail());
		response.setPhoneNo(user.getPhoneNo());
		response.setLicenseNo(user.getLicenseNo());
		response.setAadharNo(user.getAadharNo());

		response.setHouseNo(user.getHouseNo());
		response.setBuildingName(user.getBuildingName());
		response.setStreetName(user.getStreetName());
		response.setArea(user.getArea());
		response.setPincode(user.getPincode());

		if (user.getGender() != null)
			response.setGender(user.getGender().name());

		return response;
	}

	@Override
	public boolean hasUserWithEmail(String email) {

		return userRepo.findByEmail(email).isPresent();
	}

	@Override
	public boolean updateProfile(String userEmail, UpdateProfileRequest request) {
		// Find user by email
		User user = userRepo.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Handle password update if provided
		if (request.getPassword() != null && !request.getPassword().isEmpty()) {
			// Validate current password is provided
			if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
				throw new IllegalArgumentException("Current password is required to update password");
			}
			// Verify current password matches
			if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
				throw new IllegalArgumentException("Current password is incorrect");
			}
			// Encode and set new password
			user.setPassword(encoder.encode(request.getPassword()));
		}

		// Update name if provided
		if (request.getName() != null && !request.getName().isEmpty()) {
			user.setName(request.getName());
		}

		// Update phone number if provided (check for duplicates)
		if (request.getPhoneNo() != null && !request.getPhoneNo().isEmpty()) {
			// Check if phone number is different from current
			if (!request.getPhoneNo().equals(user.getPhoneNo())) {
				// Check if another user already has this phone number
				Optional<User> existingUserWithPhone = userRepo.findByPhoneNo(request.getPhoneNo());
				if (existingUserWithPhone.isPresent() && !existingUserWithPhone.get().getId().equals(user.getId())) {
					throw new IllegalArgumentException("Phone number already exists");
				}
			}
			user.setPhoneNo(request.getPhoneNo());
		}

		// Update license number if provided (check for duplicates)
		if (request.getLicenseNo() != null && !request.getLicenseNo().isEmpty()) {
			// Check if license number is different from current
			if (!request.getLicenseNo().equals(user.getLicenseNo())) {
				// Check if another user already has this license number
				Optional<User> existingUserWithLicense = userRepo.findByLicenseNo(request.getLicenseNo());
				if (existingUserWithLicense.isPresent()
						&& !existingUserWithLicense.get().getId().equals(user.getId())) {
					throw new IllegalArgumentException("License number already exists");
				}
			}
			user.setLicenseNo(request.getLicenseNo());
		}

		// Update Aadhar number if provided (check for duplicates)
		if (request.getAadharNo() != null && !request.getAadharNo().isEmpty()) {
			// Check if Aadhar number is different from current
			if (!request.getAadharNo().equals(user.getAadharNo())) {
				// Check if another user already has this Aadhar number
				Optional<User> existingUserWithAadhar = userRepo.findByAadharNo(request.getAadharNo());
				if (existingUserWithAadhar.isPresent() && !existingUserWithAadhar.get().getId().equals(user.getId())) {
					throw new IllegalArgumentException("Aadhar number already exists");
				}
			}
			user.setAadharNo(request.getAadharNo());
		}

		// Update address fields if provided
		if (request.getHouseNo() != null) {
			user.setHouseNo(request.getHouseNo());
		}
		if (request.getBuildingName() != null) {
			user.setBuildingName(request.getBuildingName());
		}
		if (request.getStreetName() != null) {
			user.setStreetName(request.getStreetName());
		}
		if (request.getArea() != null) {
			user.setArea(request.getArea());
		}
		if (request.getPincode() != null) {
			user.setPincode(request.getPincode());
		}

		// Update gender if provided
		if (request.getGender() != null && !request.getGender().isEmpty()) {
			try {
				user.setGender(Gender.valueOf(request.getGender()));
			} catch (IllegalArgumentException e) {
				
			}
		}

		// Save updated user
		try {
			userRepo.save(user);
			return true;
		} catch (Exception e) {
			// Handle unique constraint violations
			throw new IllegalArgumentException("Update failed: " + e.getMessage());
		}
	}

	@Override
	public boolean deleteProfile(String userEmail) {
		// Find user by email
		User user = userRepo.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Delete user from database
		userRepo.delete(user);
		return true;
	}

}
