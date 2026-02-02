package com.carrental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrental.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	Optional<Payment> findByBookingId(Integer bookingId);

}
