package com.project.Airbnb.service.impl;

import com.project.Airbnb.entity.Inventory;
import com.project.Airbnb.entity.Room;
import com.project.Airbnb.repository.InventoryRepository;
import com.project.Airbnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today=LocalDate.now();
        LocalDate endDate=today.plusYears(1);
        for (;!today.isAfter(endDate);today=today.plusDays(1))
        {
            Inventory inventory=Inventory.builder().
                    room(room).
                    hotel(room.getHotel()).
                    bookedCount(0).
                    city(room.getHotel().getCity()).
                    surgeFactor(BigDecimal.ONE).
                    date(today).
                    price(room.getBasePrice()).
                    totalCount(room.getTotalCount()).closed(false).
                    build();
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteFutureInventories(Room roomCategory) {
        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByDateAfterAndRoom(today, roomCategory);
    }
}
