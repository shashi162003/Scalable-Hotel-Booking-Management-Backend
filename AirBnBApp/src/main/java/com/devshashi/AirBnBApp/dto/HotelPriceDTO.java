package com.devshashi.AirBnBApp.dto;

import com.devshashi.AirBnBApp.entity.Hotel;
import lombok.Data;

@Data
public class HotelPriceDTO {
    private Hotel hotel;
    private Double price;

    public HotelPriceDTO() {}

    public HotelPriceDTO(Hotel hotel, Double price) {
        this.hotel = hotel;
        this.price = price;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
