package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.*;
import com.devshashi.AirBnBApp.entity.Hotel;
import com.devshashi.AirBnBApp.entity.Inventory;
import com.devshashi.AirBnBApp.entity.Room;
import com.devshashi.AirBnBApp.entity.User;
import com.devshashi.AirBnBApp.exception.ResourceNotFoundException;
import com.devshashi.AirBnBApp.repository.HotelMinPriceRepository;
import com.devshashi.AirBnBApp.repository.InventoryRepository;
import com.devshashi.AirBnBApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.devshashi.AirBnBApp.util.AppUtils.getCurrentUser;

@Service
public class InventoryServiceImpl implements InventoryService{
    Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, ModelMapper modelMapper, HotelMinPriceRepository hotelMinPriceRepository,
                                RoomRepository roomRepository) {
        this.inventoryRepository = inventoryRepository;
        this.modelMapper = modelMapper;
        this.hotelMinPriceRepository = hotelMinPriceRepository;
        this.roomRepository = roomRepository;
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
    public Page<HotelPriceDTO> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("Searching hotels for {} city, from {} to {}", hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());

        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;

        Page<HotelPriceDTO> hotelPage = hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate(), hotelSearchRequest.getRoomsCount(), dateCount, pageable);

        return hotelPage;
    }

    @Override
    public List<InventoryDTO> getAllInventoryByRoom(Long roomId) {
        log.info("Getting all inventory by room for room with ID : " + roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID : " + roomId));
        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())) throw new AccessDeniedException("You are not the owner of the room with ID : " + roomId);

        return inventoryRepository.findByRoomOrderByDate(room).stream().map((element) -> modelMapper.map(element, InventoryDTO.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDTO updateInventoryRequestDTO) {
        log.info("Updating all inventory by room for room with ID : {} between date range : {} - {}", roomId, updateInventoryRequestDTO.getStartDate(), updateInventoryRequestDTO.getEndDate());
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID : " + roomId));
        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())) throw new AccessDeniedException("You are not the owner of the room with ID : " + roomId);

        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId, updateInventoryRequestDTO.getStartDate(), updateInventoryRequestDTO.getEndDate());

        inventoryRepository.updateInventory(roomId, updateInventoryRequestDTO.getStartDate(), updateInventoryRequestDTO.getEndDate(), updateInventoryRequestDTO.getClosed(), updateInventoryRequestDTO.getSurgeFactor());
    }
}
