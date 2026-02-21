package com.devshashi.AirBnBApp.dto;

import lombok.Data;

import java.util.List;

@Data
public class HotelInfoDTO {
    private HotelDTO hotel;
    private List<RoomDTO> rooms;

    public HotelInfoDTO(){}

    public HotelInfoDTO(HotelDTO hotel, List<RoomDTO> rooms) {
        this.hotel = hotel;
        this.rooms = rooms;
    }

    public HotelDTO getHotel() {
        return hotel;
    }

    public void setHotel(HotelDTO hotel) {
        this.hotel = hotel;
    }

    public List<RoomDTO> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomDTO> rooms) {
        this.rooms = rooms;
    }
}
