package com.devshashi.AirBnBApp.strategy;

import com.devshashi.AirBnBApp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public class HolidayPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    public HolidayPricingStrategy(PricingStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        boolean isTodayHoliday = true; // call an api or check with local data
        if(isTodayHoliday){
            price = price.multiply(BigDecimal.valueOf(1.25));
        }
        return price;
    }
}
