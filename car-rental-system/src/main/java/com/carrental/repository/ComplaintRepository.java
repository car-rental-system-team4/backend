package com.carrental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrental.entity.Complaint;
import com.carrental.enums.ComplaintStatus;

public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {

	List<Complaint> findByUserId(Integer userId);

	List<Complaint> findByStatus(ComplaintStatus status);

}
