package com.devshashi.AirBnBApp.strategy;

import com.devshashi.AirBnBApp.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
