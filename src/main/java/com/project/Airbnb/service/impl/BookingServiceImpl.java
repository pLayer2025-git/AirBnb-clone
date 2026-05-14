package com.project.Airbnb.service.impl;

import com.project.Airbnb.dto.BookingDto;
import com.project.Airbnb.dto.BookingRequest;
import com.project.Airbnb.dto.GuestDto;
import com.project.Airbnb.dto.HotelSearchRequest;
import com.project.Airbnb.entity.*;
import com.project.Airbnb.entity.enums.BookingStatus;
import com.project.Airbnb.exception.ResourceNotFoundException;
import com.project.Airbnb.repository.*;
import com.project.Airbnb.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public BookingDto intializeBooking(BookingRequest bookingRequest) {
        log.info("Initialising booking for hotel : {}, room: {}, date {}-{}", bookingRequest.getHotelId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());

        Hotel hotel=hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(()->
                        new ResourceNotFoundException("hotel not found with id "+bookingRequest.getHotelId()));
        Room room=roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(()->
                        new ResourceNotFoundException("room not found with id "+bookingRequest.getRoomId()));
        List<Inventory> inventoryList=inventoryRepository
                .findAndLockAvailableInventory(room.getId()
                ,bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());
        long dateCount= ChronoUnit.DAYS.between(
                bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate()
        )+1;
        if ((inventoryList.size()) != dateCount)
        {
            throw new IllegalStateException("Room is not available anymore");
        }
        //TODO reserve the room/update the booked count of inventory

        for (Inventory inventory:inventoryList) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }
            inventoryRepository.saveAll(inventoryList);
        //create the booking



        Booking booking=Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(BigDecimal.TEN)
                .build();
        booking=bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Adding guests for booking with id: {}", bookingId);
        Booking booking =bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("Booking not found with id "+ bookingId));
        if (hasBookingExpired(booking)){
            throw new IllegalStateException("Booking seesion is expired");
        }
        if (booking.getBookingStatus()!=BookingStatus.RESERVED)
        {
            throw new IllegalStateException("Booking is under reserved state ,cannot add guests");
        }
        for (GuestDto guestDto:guestDtoList)
        {
            Guest guest=modelMapper.map(guestDto, Guest.class);
            guest.setUser(getCurrentUser());
            guest= guestRepository.save(guest);
            booking.getGuests().add(guest);

        }
        booking=bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }
    public boolean hasBookingExpired(Booking booking)
    {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser()
    {
        User user=new User();
        user.setId(1L);
        return user;//todo remove dummy user
    }
}
