package com.devshashi.AirBnBApp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateInventoryRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal surgeFactor;
    private Boolean closed;

    public UpdateInventoryRequestDTO() {}

    public UpdateInventoryRequestDTO(LocalDate startDate, LocalDate endDate, BigDecimal surgeFactor, Boolean closed) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.surgeFactor = surgeFactor;
        this.closed = closed;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getSurgeFactor() {
        return surgeFactor;
    }

    public void setSurgeFactor(BigDecimal surgeFactor) {
        this.surgeFactor = surgeFactor;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }
}
