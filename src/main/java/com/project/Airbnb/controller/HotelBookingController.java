package com.project.Airbnb.controller;

import com.project.Airbnb.dto.BookingDto;
import com.project.Airbnb.dto.BookingRequest;
import com.project.Airbnb.dto.GuestDto;
import com.project.Airbnb.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/bookings")
@RestController
public class HotelBookingController {
    private final BookingService bookingService;
    @PostMapping("/init")
    public ResponseEntity<BookingDto> initializaBooking(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.intializeBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId,
                                                @RequestBody List<GuestDto> guestDtoList) {
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDtoList));
    }

}
