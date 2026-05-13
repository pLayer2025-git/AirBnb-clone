package com.project.Airbnb.controller;

import com.project.Airbnb.dto.HotelDto;
import com.project.Airbnb.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "admin/hotels")
public class HotelController {
    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels()
    {
        return new ResponseEntity<>(hotelService.getAllHotels(),HttpStatus.FOUND);
    }
    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto){
        log.info("Attempting to create a new hotel with name: "+hotelDto.getName());
        HotelDto hotelDto1=hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(hotelDto1, HttpStatus.CREATED);
    }
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId)
    {
        HotelDto hotelDto=hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotelDto);
    }
    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long hotelId,@RequestBody HotelDto hotelDto) {
        HotelDto hotelDto1 = hotelService.updateHotelById(hotelId, hotelDto);
        log.info("hotel id :{}",hotelDto1.getId());
        return ResponseEntity.ok(hotelDto1);
    }
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long hotelId,@RequestBody HotelDto hotelDto) {
      hotelService.deleteHotelById(hotelId);
       return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{hotelId}")
    public ResponseEntity<Void> activateHotelById(@PathVariable Long hotelId)
    {
      hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }
}
