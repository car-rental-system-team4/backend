package com.carrental.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrental.dto.PaymentRequest;
import com.carrental.dto.PaymentResponse;
import com.carrental.dto.PaymentStatusUpdateRequest;
import com.carrental.entity.Booking;
import com.carrental.entity.Payment;
import com.carrental.entity.User;
import com.carrental.enums.BookingStatus;
import com.carrental.enums.PaymentStatus;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.PaymentRepository;
import com.carrental.repository.UserRepository;
import com.carrental.client.AuditClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;
	private final AuditClient auditClient;

	@Override
	@Transactional
	public PaymentResponse createPayment(String userEmail, PaymentRequest request) {
		// Find user
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Find booking
		Booking booking = bookingRepository.findByIdAndUserId(request.getBookingId(), user.getId())
				.orElseThrow(() -> new IllegalArgumentException("Booking not found"));

		// Check if payment already exists (One booking → One payment)
		if (paymentRepository.findByBookingId(request.getBookingId()).isPresent()) {
			throw new IllegalArgumentException("Payment already exists for this booking");
		}

		// Validate booking status
		if (booking.getStatus() == BookingStatus.CANCELLED) {
			throw new IllegalArgumentException("Cannot create payment for cancelled booking");
		}

		// Create payment
		Payment payment = new Payment();
		payment.setBooking(booking);
		payment.setAmount(booking.getTotalAmount());
		payment.setPaymentMethod(request.getPaymentMethod());
		payment.setStatus(PaymentStatus.PENDING);
		payment.setTransactionId(request.getTransactionId() != null ? request.getTransactionId()
				: "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

		// Save payment
		Payment savedPayment = paymentRepository.save(payment);

		// Update payment status to COMPLETED (simulating successful payment)
		// In real scenario, this would be done after payment gateway confirmation
		savedPayment.setStatus(PaymentStatus.COMPLETED);
		savedPayment.setPaymentDate(LocalDateTime.now());
		savedPayment = paymentRepository.save(savedPayment);

		// Update booking status to CONFIRMED
		booking.setStatus(BookingStatus.CONFIRMED);
		bookingRepository.save(booking);

		// Audit Log
		auditClient.logActivity("PAYMENT_SUCCESS", userEmail,
				"Payment ID: " + savedPayment.getId() + ", Amount: " + savedPayment.getAmount());

		// Convert to response
		return convertToResponse(savedPayment);
	}

	@Override
	public PaymentResponse getPaymentByBookingId(String userEmail, Integer bookingId) {
		// Find user
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Find booking and verify ownership
		Booking booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
				.orElseThrow(() -> new IllegalArgumentException("Booking not found"));

		// Find payment
		Payment payment = paymentRepository.findByBookingId(bookingId)
				.orElseThrow(() -> new IllegalArgumentException("Payment not found for this booking"));

		return convertToResponse(payment);
	}

	@Override
	@Transactional
	public PaymentResponse updatePaymentStatus(Integer paymentId, PaymentStatusUpdateRequest request) {
		// Find payment
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new IllegalArgumentException("Payment not found"));

		// Parse and validate status
		PaymentStatus newStatus;
		try {
			newStatus = PaymentStatus.valueOf(request.getStatus().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid payment status: " + request.getStatus());
		}

		// Update status
		payment.setStatus(newStatus);

		// If status is COMPLETED, set payment date
		if (newStatus == PaymentStatus.COMPLETED && payment.getPaymentDate() == null) {
			payment.setPaymentDate(LocalDateTime.now());

			// Update booking status to CONFIRMED
			Booking booking = payment.getBooking();
			if (booking.getStatus() == BookingStatus.PENDING) {
				booking.setStatus(BookingStatus.CONFIRMED);
				bookingRepository.save(booking);
			}
		}

		// Save updated payment
		Payment updatedPayment = paymentRepository.save(payment);

		return convertToResponse(updatedPayment);
	}

	// Helper method to convert Payment entity to PaymentResponse DTO
	private PaymentResponse convertToResponse(Payment payment) {
		PaymentResponse response = new PaymentResponse();
		response.setId(payment.getId());
		response.setBookingId(payment.getBooking().getId());
		response.setAmount(payment.getAmount());
		response.setPaymentMethod(payment.getPaymentMethod());
		response.setStatus(payment.getStatus());
		response.setTransactionId(payment.getTransactionId());
		response.setPaymentDate(payment.getPaymentDate());
		response.setCreatedAt(payment.getCreatedAt());
		response.setUpdatedAt(payment.getUpdatedAt());
		response.setUserId(payment.getBooking().getUser().getId());
		response.setUserName(payment.getBooking().getUser().getName());
		response.setVehicleId(payment.getBooking().getVehicle().getId());
		response.setVehicleMake(payment.getBooking().getVehicle().getMake());
		response.setVehicleModel(payment.getBooking().getVehicle().getModel());
		return response;
	}

}
