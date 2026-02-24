package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.HotelDTO;
import com.devshashi.AirBnBApp.dto.HotelInfoDTO;
import com.devshashi.AirBnBApp.entity.Hotel;

import java.util.List;

public interface HotelService {
    HotelDTO createNewHotel(HotelDTO hotelDTO);
    HotelDTO getHotelById(Long id);
    HotelDTO updateHotelById(Long id, HotelDTO hotelDTO);
    void deleteHotelById(Long id);
    void activateHotel(Long id);
    HotelInfoDTO getHotelInfoById(Long hotelId);

    List<HotelDTO> getAllHotels();
}
