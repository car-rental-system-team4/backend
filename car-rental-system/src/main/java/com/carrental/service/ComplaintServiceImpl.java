package com.carrental.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.carrental.dto.ComplaintRequest;
import com.carrental.dto.ComplaintResponse;
import com.carrental.dto.ComplaintResolutionRequest;
import com.carrental.entity.Complaint;
import com.carrental.entity.User;
import com.carrental.enums.ComplaintStatus;
import com.carrental.repository.ComplaintRepository;
import com.carrental.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

	private final ComplaintRepository complaintRepository;
	private final UserRepository userRepository;

	@Override
	public ComplaintResponse createComplaint(String userEmail, ComplaintRequest request) {
		// Get user
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Create complaint
		Complaint complaint = new Complaint();
		complaint.setUser(user);
		complaint.setSubject(request.getSubject());
		complaint.setDescription(request.getDescription());
		complaint.setBookingId(request.getBookingId());
		complaint.setStatus(ComplaintStatus.PENDING);

		Complaint savedComplaint = complaintRepository.save(complaint);
		return convertToResponse(savedComplaint);
	}

	@Override
	public List<ComplaintResponse> getUserComplaints(String userEmail) {
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		List<Complaint> complaints = complaintRepository.findByUserId(user.getId());
		return complaints.stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<ComplaintResponse> getAllComplaints() {
		List<Complaint> complaints = complaintRepository.findAll();
		return complaints.stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public ComplaintResponse resolveComplaint(Integer complaintId, ComplaintResolutionRequest request) {
		Complaint complaint = complaintRepository.findById(complaintId)
				.orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

		complaint.setStatus(ComplaintStatus.RESOLVED);
		complaint.setAdminResponse(request.getAdminResponse());

		Complaint savedComplaint = complaintRepository.save(complaint);
		return convertToResponse(savedComplaint);
	}

	private ComplaintResponse convertToResponse(Complaint complaint) {
		ComplaintResponse response = new ComplaintResponse();
		response.setId(complaint.getId());
		response.setUserId(complaint.getUser().getId());
		response.setUserName(complaint.getUser().getName());
		response.setUserEmail(complaint.getUser().getEmail());
		response.setSubject(complaint.getSubject());
		response.setDescription(complaint.getDescription());
		response.setBookingId(complaint.getBookingId());
		response.setStatus(complaint.getStatus());
		response.setAdminResponse(complaint.getAdminResponse());
		response.setResolvedAt(complaint.getResolvedAt());
		response.setCreatedAt(complaint.getCreatedAt());
		response.setUpdatedAt(complaint.getUpdatedAt());
		return response;
	}

}
