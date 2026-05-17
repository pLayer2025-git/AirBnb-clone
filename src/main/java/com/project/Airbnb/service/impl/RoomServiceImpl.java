package com.project.Airbnb.service.impl;

import com.project.Airbnb.dto.RoomDto;
import com.project.Airbnb.entity.Hotel;
import com.project.Airbnb.entity.Room;
import com.project.Airbnb.entity.User;
import com.project.Airbnb.exception.ResourceNotFoundException;
import com.project.Airbnb.exception.UnAuthorizedException;
import com.project.Airbnb.repository.HotelRepository;
import com.project.Airbnb.repository.RoomRepository;
import com.project.Airbnb.service.InventoryService;
import com.project.Airbnb.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.Airbnb.utils.AppUtils.getCurrentUser;

@RequiredArgsConstructor
@Service
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() ->
                new ResourceNotFoundException("No hotel found with id " + hotelId));
//        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (!user.equals(hotel.getOwner())){
//            throw new UnAuthorizedException("this user does not own this hotel with id "+ hotelId);
//        }
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);
        if(hotel.getActive())
        {
            inventoryService.initializeRoomForAYear(room);
        }
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository.
                findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));
        return hotel.getRooms().stream().
                map(element -> modelMapper.map(element, RoomDto.class)).
                collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting  room  with ID: {}", roomId);
       Room room=roomRepository.
                findById(roomId).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID: "+roomId));
     return modelMapper.map(room,RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorizedException("this user does not own this hotel with id "+ roomId);
        }
        inventoryService.deleteFutureInventories(room);
        roomRepository.deleteById(roomId);
    }

    @Override
    public RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto roomDto) {
        log.info("Updating the room with ID: {}", roomId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorizedException("This user does not own this hotel with id: "+hotelId);
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));

        modelMapper.map(roomDto, room);
        room.setId(roomId);

//        TODO: if price or inventory is updated, then update the inventory for this room
        room = roomRepository.save(room);

        return modelMapper.map(room, RoomDto.class);
    }
}
