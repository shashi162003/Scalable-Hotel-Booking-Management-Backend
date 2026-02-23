package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.HotelDTO;
import com.devshashi.AirBnBApp.dto.HotelInfoDTO;
import com.devshashi.AirBnBApp.dto.RoomDTO;
import com.devshashi.AirBnBApp.entity.Hotel;
import com.devshashi.AirBnBApp.entity.Room;
import com.devshashi.AirBnBApp.entity.User;
import com.devshashi.AirBnBApp.exception.ResourceNotFoundException;
import com.devshashi.AirBnBApp.exception.UnauthorizedException;
import com.devshashi.AirBnBApp.repository.HotelRepository;
import com.devshashi.AirBnBApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService{
    Logger log = LoggerFactory.getLogger(HotelServiceImpl.class);

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;

    public HotelServiceImpl(HotelRepository hotelRepository, ModelMapper modelMapper, InventoryService inventoryService, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.modelMapper = modelMapper;
        this.inventoryService = inventoryService;
        this.roomRepository = roomRepository;
    }

    @Override
    public HotelDTO createNewHotel(HotelDTO hotelDTO) {
        log.info("Creating a new hotel with name : {}", hotelDTO.getName());
        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);
        hotel.setActive(false);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);

        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Created a new hotel with ID : {}", savedHotel.getId());
        return modelMapper.map(savedHotel, HotelDTO.class);
    }

    @Override
    public HotelDTO getHotelById(Long id) {
        log.info("Getting the hotel with ID : {}", id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id : " + id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())) throw new UnauthorizedException("This user does not own this hotel with id : " + id);
        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    public HotelDTO updateHotelById(Long id, HotelDTO hotelDTO) {
        log.info("Updating the hotel with ID : {}", id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id : " + id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())) throw new UnauthorizedException("This user does not own this hotel with id : " + id);
        modelMapper.map(hotelDTO, hotel);
        hotel.setId(id);
        Hotel savedHotel = hotelRepository.save(hotel);
        return modelMapper.map(savedHotel, HotelDTO.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id : " + id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())) throw new UnauthorizedException("This user does not own this hotel with id : " + id);
        for(Room room: hotel.getRooms()) {
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activateHotel(Long id) {
        log.info("Activating the hotel with ID : {}", id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id : " + id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())) throw new UnauthorizedException("This user does not own this hotel with id : " + id);
        hotel.setActive(true);
        for(Room room: hotel.getRooms()) {
            inventoryService.initializeRoomForAYear(room);
        }
    }

    @Override
    public HotelInfoDTO getHotelInfoById(Long hotelId) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id : " + hotelId));
        List<RoomDTO> rooms = hotel.getRooms().stream().map((element) -> modelMapper.map(element, RoomDTO.class)).collect(Collectors.toList());
        return new HotelInfoDTO(modelMapper.map(hotel, HotelDTO.class), rooms);
    }
}
