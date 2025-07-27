package com.github.lukashindy.booking.controller;

import com.github.lukashindy.booking.dto.RoomDto;
import com.github.lukashindy.booking.dto.RoomWithDetailsDto;
import com.github.lukashindy.booking.exception.ResourceNotFoundException;
import com.github.lukashindy.booking.mapper.RoomMapper;
import com.github.lukashindy.booking.model.Room;
import com.github.lukashindy.booking.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    
    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomMapper roomMapper;
    
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomWithDetailsDto>> getRoomsByHotelId(@PathVariable Long hotelId) {
        logger.info("Getting rooms for hotel ID: {}", hotelId);
        
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        List<RoomWithDetailsDto> roomDtos = rooms.stream()
                .map(roomMapper::toDetailedDto)
                .collect(Collectors.toList());
        
        logger.info("Found {} rooms for hotel ID: {}", roomDtos.size(), hotelId);
        return ResponseEntity.ok(roomDtos);
    }
    
    @GetMapping("/room-type/{roomTypeId}")
    public ResponseEntity<List<RoomWithDetailsDto>> getRoomsByRoomTypeId(@PathVariable Long roomTypeId) {
        logger.info("Getting rooms for room type ID: {}", roomTypeId);
        
        List<Room> rooms = roomRepository.findByRoomTypeId(roomTypeId);
        List<RoomWithDetailsDto> roomDtos = rooms.stream()
                .map(roomMapper::toDetailedDto)
                .collect(Collectors.toList());
        
        logger.info("Found {} rooms for room type ID: {}", roomDtos.size(), roomTypeId);
        return ResponseEntity.ok(roomDtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RoomWithDetailsDto> getRoomById(@PathVariable Long id) {
        logger.info("Getting room with ID: {}", id);
        
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        
        RoomWithDetailsDto roomDto = roomMapper.toDetailedDto(room);
        logger.info("Found room: {}", roomDto.getRoomNumber());
        return ResponseEntity.ok(roomDto);
    }
    
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        logger.info("Getting all rooms");
        
        List<Room> rooms = roomRepository.findAll();
        List<RoomDto> roomDtos = rooms.stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
        
        logger.info("Found {} rooms", roomDtos.size());
        return ResponseEntity.ok(roomDtos);
    }
}
