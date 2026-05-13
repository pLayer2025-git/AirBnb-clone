package com.project.Airbnb.service;

import com.project.Airbnb.dto.RoomDto;

import java.util.List;

public interface RoomService{
    RoomDto createNewRoom(Long hotelId,RoomDto roomDto);
    List<RoomDto> getAllRoomInHotel(Long hotelId);
    RoomDto getRoomById(Long roomId);
    void deleteRoomById(Long id);
}
