package com.carrental.service;

import java.util.List;

import com.carrental.dto.BookingRequest;
import com.carrental.dto.BookingResponse;

public interface BookingService {

	BookingResponse createBooking(String userEmail, BookingRequest request);

	BookingResponse getBookingById(String userEmail, Integer bookingId);

	List<BookingResponse> getUserBookings(String userEmail);

	BookingResponse cancelBooking(String userEmail, Integer bookingId);

	List<BookingResponse> getVendorBookings(String vendorEmail);

}
