package com.project.Airbnb.service;


import com.project.Airbnb.entity.Room;


public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteFutureInventories(Room roomCategory);

}
