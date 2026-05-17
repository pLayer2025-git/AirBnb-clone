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
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/bookings")
@RestController
@Slf4j
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


    //upon intiating payment , backend will do checkout session to stripe and stripe will return payment url page
    //this url is stored in sessionUrl which will be go to frontend
    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long bookingId) {

        String sessionUrl = bookingService.initiatePayments(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl", sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookingId}/status")
    public ResponseEntity<Map<String, String>> getBookingStatus(@PathVariable Long bookingId) {
        return ResponseEntity.ok(Map.of("status", bookingService.getBookingStatus(bookingId)));
    }


}
