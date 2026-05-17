package com.project.Airbnb.service;

import com.project.Airbnb.dto.BookingDto;
import com.project.Airbnb.dto.BookingRequest;
import com.project.Airbnb.dto.GuestDto;
import com.stripe.model.Event;

import java.util.List;

public interface BookingService {

    BookingDto intializeBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);
}
