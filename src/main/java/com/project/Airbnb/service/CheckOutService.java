package com.project.Airbnb.service;

import com.project.Airbnb.entity.Booking;
import org.springframework.stereotype.Service;

public interface CheckOutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
