package com.project.Airbnb.service.impl;

import com.project.Airbnb.dto.HotelDto;
import com.project.Airbnb.dto.HotelPriceDto;
import com.project.Airbnb.dto.HotelSearchRequest;
import com.project.Airbnb.entity.Hotel;
import com.project.Airbnb.entity.Inventory;
import com.project.Airbnb.entity.Room;
import com.project.Airbnb.repository.HotelMinPriceRepository;
import com.project.Airbnb.repository.InventoryRepository;
import com.project.Airbnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(3);
        for (; !today.isAfter(endDate); today = today.plusDays(1)) {
            Inventory inventory = Inventory.builder().
                    room(room).
                    hotel(room.getHotel()).
                    bookedCount(0).
                    reservedCount(0).
                    city(room.getHotel().getCity()).
                    surgeFactor(BigDecimal.ONE).
                    date(today).
                    price(room.getBasePrice()).
                    totalCount(room.getTotalCount()).
                    closed(false).
                    build();
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteFutureInventories(Room roomCategory) {
        inventoryRepository.deleteByRoom(roomCategory);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        log.info("{}",hotelSearchRequest.getRoomsCount());
        long datecount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate())+1;// 1 is vey important

        //buisness logic, we can implement two inventory , one for first 90 days and other for next 270 days
        Page<HotelPriceDto> hotelPage = hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
                datecount, pageable);
        return hotelPage;

    }
}
