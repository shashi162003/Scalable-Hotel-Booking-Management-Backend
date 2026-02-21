package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.RoomDTO;
import com.devshashi.AirBnBApp.entity.Hotel;
import com.devshashi.AirBnBApp.entity.Room;
import com.devshashi.AirBnBApp.exception.ResourceNotFoundException;
import com.devshashi.AirBnBApp.repository.HotelRepository;
import com.devshashi.AirBnBApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService{
    Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

    public RoomServiceImpl(RoomRepository roomRepository, HotelRepository hotelRepository, ModelMapper modelMapper, InventoryService inventoryService) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.modelMapper = modelMapper;
        this.inventoryService = inventoryService;
    }

    @Override
    @Transactional
    public RoomDTO createNewRoom(Long hotelId, RoomDTO roomDTO) {
        log.info("Creating a new room in hotel with ID : " + hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id : " + hotelId));
        Room room = modelMapper.map(roomDTO, Room.class);
        room.setHotel(hotel);
        Room savedHotel = roomRepository.save(room);
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }
        return modelMapper.map(savedHotel, RoomDTO.class);
    }

    @Override
    public List<RoomDTO> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with ID : " + hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id : " + hotelId));
        return hotel.getRooms()
                .stream()
                .map(element -> modelMapper.map(element, RoomDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDTO getRoomById(Long roomId) {
        log.info("Getting the room with ID : " + roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id : " + roomId));
        return modelMapper.map(room, RoomDTO.class);
    }

    @Transactional
    @Override
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with ID : " + roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id : " + roomId));
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }
}
