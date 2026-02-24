package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.RoomDTO;

import java.util.List;

public interface RoomService {
    RoomDTO createNewRoom(Long hotelId, RoomDTO roomDTO);
    List<RoomDTO> getAllRoomsInHotel(Long hotelId);
    RoomDTO getRoomById(Long roomId);
    void deleteRoomById(Long roomId);

    RoomDTO updateRoomById(Long roomId, RoomDTO roomDTO);
}
