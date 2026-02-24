package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.entity.Booking;

public interface CheckoutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
