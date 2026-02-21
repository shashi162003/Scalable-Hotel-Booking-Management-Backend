package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.BookingDTO;
import com.devshashi.AirBnBApp.dto.BookingRequest;
import com.devshashi.AirBnBApp.dto.GuestDTO;

import java.util.List;

public interface BookingService {
    BookingDTO initializeBooking(BookingRequest bookingRequest);

    BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDTOList);
}
