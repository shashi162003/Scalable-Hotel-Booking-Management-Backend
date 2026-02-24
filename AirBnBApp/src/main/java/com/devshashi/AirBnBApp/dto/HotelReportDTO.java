package com.devshashi.AirBnBApp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HotelReportDTO {
    private Long bookingCount;
    private BigDecimal totalRevenue;
    private BigDecimal avgRevenue;

    public HotelReportDTO() {}

    public HotelReportDTO(Long bookingCount, BigDecimal totalRevenue, BigDecimal avgRevenue) {
        this.bookingCount = bookingCount;
        this.totalRevenue = totalRevenue;
        this.avgRevenue = avgRevenue;
    }

    public Long getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(Long bookingCount) {
        this.bookingCount = bookingCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAvgRevenue() {
        return avgRevenue;
    }

    public void setAvgRevenue(BigDecimal avgRevenue) {
        this.avgRevenue = avgRevenue;
    }
}
