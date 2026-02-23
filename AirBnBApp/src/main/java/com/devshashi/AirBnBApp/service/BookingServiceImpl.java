package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.BookingDTO;
import com.devshashi.AirBnBApp.dto.BookingRequest;
import com.devshashi.AirBnBApp.dto.GuestDTO;
import com.devshashi.AirBnBApp.entity.*;
import com.devshashi.AirBnBApp.entity.enums.BookingStatus;
import com.devshashi.AirBnBApp.exception.ResourceNotFoundException;
import com.devshashi.AirBnBApp.exception.UnauthorizedException;
import com.devshashi.AirBnBApp.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    public BookingServiceImpl(BookingRepository bookingRepository, HotelRepository hotelRepository, RoomRepository roomRepository, InventoryRepository inventoryRepository, GuestRepository guestRepository, ModelMapper modelMapper) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.inventoryRepository = inventoryRepository;
        this.guestRepository = guestRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public BookingDTO initializeBooking(BookingRequest bookingRequest) {
        log.info("Initializing booking for hotel : {}, room : {}, date: {} - {}", bookingRequest.getHotelId(), bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id : " + bookingRequest.getHotelId()));
        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id : " + bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if(inventoryList.size() != daysCount){
            throw new IllegalStateException("Room is not available anymore");
        }

        // Reserving the rooms
        for(Inventory inventory: inventoryList){
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);

        // TODO: Calculate dynamic amount

        Booking booking = new Booking();

        booking.setBookingStatus(BookingStatus.RESERVED);
        booking.setHotel(hotel);
        booking.setRoom(room);
        booking.setCheckInDate(bookingRequest.getCheckInDate());
        booking.setCheckOutDate(bookingRequest.getCheckOutDate());
        booking.setUser(getCurrentUser());
        booking.setRoomsCount(bookingRequest.getRoomsCount());
        booking.setAmount(BigDecimal.TEN);

        Booking savedBooking = bookingRepository.save(booking);
        return modelMapper.map(savedBooking, BookingDTO.class);
    }

    @Override
    @Transactional
    public BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDTOList) {
        log.info("Adding guests for booking with id : {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id : " + bookingId));

        User user = getCurrentUser();

        if(!user.equals(booking.getUser())){
            throw new UnauthorizedException("Booking does not belong to this user with id : " + user.getId());
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");
        }

        for(GuestDTO guestDTO: guestDTOList){
            Guest guest = modelMapper.map(guestDTO, Guest.class);
            guest.setUser(user);
            Guest savedGuest = guestRepository.save(guest);
            booking.getGuests().add(savedGuest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        Booking savedBooking = bookingRepository.save(booking);
        return modelMapper.map(savedBooking, BookingDTO.class);
    }

    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }
}
