package com.project.Airbnb.service;

import com.project.Airbnb.dto.BookingDto;
import com.project.Airbnb.dto.BookingRequest;
import com.project.Airbnb.dto.GuestDto;

import java.util.List;

public interface BookingService {

    BookingDto intializeBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
