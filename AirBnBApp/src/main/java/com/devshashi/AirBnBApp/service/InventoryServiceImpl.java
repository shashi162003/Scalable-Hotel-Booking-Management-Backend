package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.entity.Inventory;
import com.devshashi.AirBnBApp.entity.Room;
import com.devshashi.AirBnBApp.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class InventoryServiceImpl implements InventoryService{
    Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for(; !today.isAfter(endDate); today = today.plusDays(1)){
            Inventory inventory = new Inventory();
            inventory.setHotel(room.getHotel());
            inventory.setRoom(room);
            inventory.setBookedCount(0);
            inventory.setCity(room.getHotel().getCity());
            inventory.setDate(today);
            inventory.setPrice(room.getBasePrice());
            inventory.setSurgeFactor(BigDecimal.ONE);
            inventory.setTotalCount(room.getTotalCount());
            inventory.setClosed(false);
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteFutureInventories(Room room) {
        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByDateAfterAndRoom(today, room);
    }
}
