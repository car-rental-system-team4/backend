package com.carrental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrental.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);

	Optional<User> findByPhoneNo(String phoneNo);

	Optional<User> findByLicenseNo(String licenseNo);

	Optional<User> findByAadharNo(String aadharNo);
}
