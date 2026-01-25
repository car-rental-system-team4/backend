package com.carrental.dto;

import lombok.Data;

@Data
public class RegisterRequest {

	private String name;
	private String email;
	private String password;
	private String phoneNo;

	private String licenseNo;
	private String aadharNo;

	private String houseNo;
	private String buildingName;
	private String streetName;
	private String area;
	private String pincode;

	private String role;
	private String gender;

}
