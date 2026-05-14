package com.project.Airbnb.service.impl;

import com.project.Airbnb.dto.HotelDto;
import com.project.Airbnb.dto.HotelInfoDto;
import com.project.Airbnb.dto.RoomDto;
import com.project.Airbnb.entity.Hotel;
import com.project.Airbnb.entity.Room;
import com.project.Airbnb.exception.ResourceNotFoundException;
import com.project.Airbnb.repository.HotelRepository;
import com.project.Airbnb.repository.RoomRepository;
import com.project.Airbnb.service.HotelService;
import com.project.Airbnb.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;
    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating new hotel with name {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);//by default
        hotel = hotelRepository.save(hotel);
        log.info("Created a new hotel with ID: {}", hotelDto.getId());
        return modelMapper.map(hotel, HotelDto.class);

    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with ID :{}", id);
        Hotel hotel = hotelRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: {}" + id));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating the hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No hotel found with id " + id)
        );
        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        log.info("Hotel updated with id: {}", hotel.getId());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No hotel found with id " + id)
        );
        //TODO deletet the future inventory for this hotel
        for (Room room : hotel.getRooms()) {
            inventoryService.deleteFutureInventories(room);
            roomRepository.deleteById(room.getId());
        }

        hotelRepository.deleteById(id);


    }

    @Override
    public void activateHotel(Long id) {
        log.info("Activating the hotel with ID :{}", id);
        Hotel hotel = hotelRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: {}" + id));
        hotel.setActive(true);
        //TODO create inventory for all the room for this hotels

        // assuming only do it once
        for(Room room: hotel.getRooms()) {
            inventoryService.initializeRoomForAYear(room);
        }
    }

    @Override
    public List<HotelDto> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream().map(elements -> modelMapper.
                map(elements, HotelDto.class)).collect(Collectors.toList());
    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel=hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("no hotel found with id "+ hotelId));
        List<RoomDto> rooms= hotel.getRooms().stream().
                map((element) -> modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList());
        return new HotelInfoDto(modelMapper.map(hotel, HotelDto.class),rooms);
    }
}
