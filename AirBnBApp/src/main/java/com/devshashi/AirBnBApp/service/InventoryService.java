package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.HotelDTO;
import com.devshashi.AirBnBApp.dto.HotelSearchRequest;
import com.devshashi.AirBnBApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteAllInventories(Room room);

    Page<HotelDTO> searchHotels(HotelSearchRequest hotelSearchRequest);
}
