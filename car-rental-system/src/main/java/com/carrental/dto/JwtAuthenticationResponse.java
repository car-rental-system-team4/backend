package com.carrental.dto;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {
	private String token;
	private String role;
	private String name;
	private Integer userId;
	private String email;

	private String phoneNo;
	private String licenseNo;
	private String aadharNo;

	private String houseNo;
	private String buildingName;
	private String streetName;
	private String area;
	private String pincode;

	private String gender;
}
