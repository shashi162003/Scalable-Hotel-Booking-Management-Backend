package com.devshashi.AirBnBApp.controller;

import com.devshashi.AirBnBApp.dto.HotelDTO;
import com.devshashi.AirBnBApp.dto.HotelInfoDTO;
import com.devshashi.AirBnBApp.dto.HotelSearchRequest;
import com.devshashi.AirBnBApp.service.HotelService;
import com.devshashi.AirBnBApp.service.InventoryService;
import com.devshashi.AirBnBApp.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    public HotelBrowseController(InventoryService inventoryService, HotelService hotelService) {
        this.inventoryService = inventoryService;
        this.hotelService = hotelService;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDTO>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){
        Page<HotelDTO> page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDTO> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
