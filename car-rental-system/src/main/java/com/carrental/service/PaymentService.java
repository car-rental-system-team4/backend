package com.carrental.service;

import com.carrental.dto.PaymentRequest;
import com.carrental.dto.PaymentResponse;
import com.carrental.dto.PaymentStatusUpdateRequest;

public interface PaymentService {

	PaymentResponse createPayment(String userEmail, PaymentRequest request);

	PaymentResponse getPaymentByBookingId(String userEmail, Integer bookingId);

	PaymentResponse updatePaymentStatus(Integer paymentId, PaymentStatusUpdateRequest request);

}
