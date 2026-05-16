package com.project.Airbnb.controller;

import com.project.Airbnb.dto.HotelDto;
import com.project.Airbnb.dto.HotelInfoDto;
import com.project.Airbnb.dto.HotelPriceDto;
import com.project.Airbnb.dto.HotelSearchRequest;
import com.project.Airbnb.service.HotelService;
import com.project.Airbnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/hotels")
public class HotelBrowseController {
private final InventoryService inventoryService;
private final HotelService hotelService;
    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest)
    {

        Page<HotelPriceDto> page=inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }
    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId)
    {
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
