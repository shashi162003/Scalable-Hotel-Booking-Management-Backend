package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.entity.Room;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteFutureInventories(Room room);
}
