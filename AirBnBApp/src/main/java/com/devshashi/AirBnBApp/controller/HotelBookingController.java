package com.devshashi.AirBnBApp.controller;

import com.devshashi.AirBnBApp.dto.BookingDTO;
import com.devshashi.AirBnBApp.dto.BookingRequest;
import com.devshashi.AirBnBApp.dto.GuestDTO;
import com.devshashi.AirBnBApp.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    public HotelBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/init")
    public ResponseEntity<BookingDTO> initializeBooking(@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDTO> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDTO> guestDTOList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDTOList));
    }
}
