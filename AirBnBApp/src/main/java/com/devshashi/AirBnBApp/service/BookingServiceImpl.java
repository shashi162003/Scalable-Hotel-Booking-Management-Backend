package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.BookingDTO;
import com.devshashi.AirBnBApp.dto.BookingRequest;
import com.devshashi.AirBnBApp.dto.GuestDTO;
import com.devshashi.AirBnBApp.entity.*;
import com.devshashi.AirBnBApp.entity.enums.BookingStatus;
import com.devshashi.AirBnBApp.exception.ResourceNotFoundException;
import com.devshashi.AirBnBApp.exception.UnauthorizedException;
import com.devshashi.AirBnBApp.repository.*;
import com.devshashi.AirBnBApp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    public BookingServiceImpl(BookingRepository bookingRepository, HotelRepository hotelRepository, RoomRepository roomRepository, InventoryRepository inventoryRepository, GuestRepository guestRepository, ModelMapper modelMapper, CheckoutService checkoutService, PricingService pricingService) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.inventoryRepository = inventoryRepository;
        this.guestRepository = guestRepository;
        this.modelMapper = modelMapper;
        this.checkoutService = checkoutService;
        this.pricingService = pricingService;
    }

    @Value("${frontend.url}")
    private String FRONTEND_URL;

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
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        Booking booking = new Booking();

        booking.setBookingStatus(BookingStatus.RESERVED);
        booking.setHotel(hotel);
        booking.setRoom(room);
        booking.setCheckInDate(bookingRequest.getCheckInDate());
        booking.setCheckOutDate(bookingRequest.getCheckOutDate());
        booking.setUser(getCurrentUser());
        booking.setRoomsCount(bookingRequest.getRoomsCount());
        booking.setAmount(totalPrice);

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

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id : " + bookingId)
        );
        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnauthorizedException("Booking does not belong to this user with id : " + user.getId());
        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }

        String sessionUrl = checkoutService.getCheckoutSession(booking, FRONTEND_URL + "/payments/success", FRONTEND_URL + "/payments/failure");

        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if(session == null) return;

            String sessionId = session.getId();
            Booking booking = bookingRepository.findByPaymentSessionId(sessionId)
                            .orElseThrow(() -> new ResourceNotFoundException("Booking not found for session id : " + sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());

            log.info("Successfully confirmed the booking for Booking ID: {}", booking.getId());
        } else {
            log.warn("Unhandled event type: {}", event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id : " + bookingId)
        );
        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnauthorizedException("Booking does not belong to this user with id : " + user.getId());
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED){
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());

        inventoryRepository.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());

        try{
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundParams);
        } catch(StripeException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id : " + bookingId)
        );
        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnauthorizedException("Booking does not belong to this user with id : " + user.getId());
        }

        return booking.getBookingStatus().name();
    }
}
