package com.project.Airbnb.service.impl;

import com.project.Airbnb.dto.BookingDto;
import com.project.Airbnb.dto.BookingRequest;
import com.project.Airbnb.dto.GuestDto;
import com.project.Airbnb.dto.HotelSearchRequest;
import com.project.Airbnb.entity.*;
import com.project.Airbnb.entity.enums.BookingStatus;
import com.project.Airbnb.exception.ResourceNotFoundException;
import com.project.Airbnb.exception.UnAuthorizedException;
import com.project.Airbnb.repository.*;
import com.project.Airbnb.service.BookingService;
import com.project.Airbnb.service.CheckOutService;
import com.project.Airbnb.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final CheckOutService checkOutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;


    @Override
    @Transactional
    public BookingDto intializeBooking(BookingRequest bookingRequest) {
        log.info("Initialising booking for hotel : {}, room: {}, date {}-{}", bookingRequest.getHotelId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());


        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("hotel not found with id " + bookingRequest.getHotelId()));
        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("room not found with id " + bookingRequest.getRoomId()));


        List<Inventory> inventoryList = inventoryRepository
                .findAndLockAvailableInventory(room.getId()
                        , bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());


        long dateCount = ChronoUnit.DAYS.between(
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()
        ) + 1;


        if ((inventoryList.size()) != dateCount) {
            throw new IllegalStateException("Room is not available anymore");
        }
        //TODO reserve the room/update the booked count of inventory

        for (Inventory inventory : inventoryList) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }
        inventoryRepository.saveAll(inventoryList);
        //create the booking
// Reserve the room/ update the booked count of inventories
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)
                .build();
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Adding guests for booking with id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorizedException("Booking does not belong to user with id " + user.getId());
        }
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking seesion is expired");
        }
        if (booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is under reserved state ,cannot add guests");
        }
        for (GuestDto guestDto : guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(getCurrentUser());
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);

        }
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new ResourceNotFoundException(
                "No booking found with id "+bookingId
        ));
        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorizedException("Booking does not belong to user with id " + user.getId());
        }
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking sesion is expired");
        }
        String sessionUrl = checkOutService.getCheckoutSession(booking,
                frontendUrl+"/payments/success", frontendUrl+"/payments/failure");
        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;
    }

    @Override
   @Transactional
    public void capturePayment(Event event) {
        log.info("{}",event.getType());
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;
            log.info("Step 1");
            String sessionId = session.getId();
            Booking booking =
                    bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() ->
                            new ResourceNotFoundException("Booking not found for session ID: "+sessionId));
            log.info("Step 2");
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            log.info("Step 3");
            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomsCount());
            log.info("Step 4");
            inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomsCount());

            log.info("Successfully confirmed the booking for Booking ID: {}", booking.getId());
        } else {
            log.warn("Unhandled event type: {}", event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorizedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        inventoryRepository.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        // handle the refund

        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorizedException("Booking does not belong to this user with id: "+user.getId());
        }

        return booking.getBookingStatus().name();
    }

public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {

        return (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();//todo remove dummy user
    }
}
