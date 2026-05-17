package com.project.Airbnb.service;


import com.project.Airbnb.dto.*;
import com.project.Airbnb.entity.Room;
import org.springframework.data.domain.Page;

import java.util.List;


public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteFutureInventories(Room roomCategory);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
