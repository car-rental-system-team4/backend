package com.carrental.service;

import java.util.List;

import com.carrental.dto.ComplaintRequest;
import com.carrental.dto.ComplaintResponse;
import com.carrental.dto.ComplaintResolutionRequest;

public interface ComplaintService {

	ComplaintResponse createComplaint(String userEmail, ComplaintRequest request);

	List<ComplaintResponse> getUserComplaints(String userEmail);

	List<ComplaintResponse> getAllComplaints();

	ComplaintResponse resolveComplaint(Integer complaintId, ComplaintResolutionRequest request);

}
