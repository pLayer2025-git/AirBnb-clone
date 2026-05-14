package com.project.Airbnb.service;


import com.project.Airbnb.dto.HotelDto;
import com.project.Airbnb.dto.HotelSearchRequest;
import com.project.Airbnb.entity.Room;
import org.springframework.data.domain.Page;


public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteFutureInventories(Room roomCategory);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
