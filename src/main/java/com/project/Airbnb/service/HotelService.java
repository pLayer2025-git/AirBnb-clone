package com.project.Airbnb.service;

import com.project.Airbnb.dto.HotelDto;
import com.project.Airbnb.entity.Hotel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface HotelService {
    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id,HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activateHotel(Long id);

    List<HotelDto> getAllHotels();
}
