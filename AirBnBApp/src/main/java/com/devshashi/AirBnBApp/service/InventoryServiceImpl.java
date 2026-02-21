package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.HotelDTO;
import com.devshashi.AirBnBApp.dto.HotelSearchRequest;
import com.devshashi.AirBnBApp.entity.Hotel;
import com.devshashi.AirBnBApp.entity.Inventory;
import com.devshashi.AirBnBApp.entity.Room;
import com.devshashi.AirBnBApp.repository.InventoryRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class InventoryServiceImpl implements InventoryService{
    Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, ModelMapper modelMapper) {
        this.inventoryRepository = inventoryRepository;
        this.modelMapper = modelMapper;
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
            inventory.setReservedCount(0);
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
    public void deleteAllInventories(Room room) {
        log.info("Deleting the inventories of room with id : {}", room.getId());
        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelDTO> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("Searching hotels for {} city, from {} to {}", hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());

        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;

        Page<Hotel> hotelPage = inventoryRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate(), hotelSearchRequest.getRoomsCount(), dateCount, pageable);

        return hotelPage.map((element) -> modelMapper.map(element, HotelDTO.class));
    }
}
