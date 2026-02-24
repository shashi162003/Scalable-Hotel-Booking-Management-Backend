package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.BookingDTO;
import com.devshashi.AirBnBApp.dto.BookingRequest;
import com.devshashi.AirBnBApp.dto.GuestDTO;
import com.devshashi.AirBnBApp.dto.HotelReportDTO;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookingService {
    BookingDTO initializeBooking(BookingRequest bookingRequest);

    BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDTOList);

    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDTO> getAllBookingsByHotelId(Long hotelId);

    HotelReportDTO getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDTO> getMyBookings();
}
