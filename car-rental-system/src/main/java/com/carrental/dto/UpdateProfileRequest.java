package com.carrental.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {

	private String name;
	private String phoneNo;
	private String licenseNo;
	private String aadharNo;
	private String houseNo;
	private String buildingName;
	private String streetName;
	private String area;
	private String pincode;
	private String gender;
	private String password; // New password
	private String currentPassword; // Required when updating password

}
