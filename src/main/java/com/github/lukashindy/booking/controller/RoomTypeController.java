package com.github.lukashindy.booking.controller;

import com.github.lukashindy.booking.dto.RoomTypeDto;
import com.github.lukashindy.booking.exception.ResourceNotFoundException;
import com.github.lukashindy.booking.mapper.RoomTypeMapper;
import com.github.lukashindy.booking.model.RoomType;
import com.github.lukashindy.booking.repository.RoomTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/room-types")
public class RoomTypeController {
    
    private static final Logger logger = LoggerFactory.getLogger(RoomTypeController.class);
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private RoomTypeMapper roomTypeMapper;
    
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomTypeDto>> getRoomTypesByHotelId(@PathVariable Long hotelId) {
        logger.info("Getting room types for hotel ID: {}", hotelId);
        
        List<RoomType> roomTypes = roomTypeRepository.findByHotelId(hotelId);
        List<RoomTypeDto> roomTypeDtos = roomTypes.stream()
                .map(roomTypeMapper::toDto)
                .collect(Collectors.toList());
        
        logger.info("Found {} room types for hotel ID: {}", roomTypeDtos.size(), hotelId);
        return ResponseEntity.ok(roomTypeDtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeDto> getRoomTypeById(@PathVariable Long id) {
        logger.info("Getting room type with ID: {}", id);
        
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", "id", id));
        
        RoomTypeDto roomTypeDto = roomTypeMapper.toDto(roomType);
        logger.info("Found room type: {}", roomTypeDto.getName());
        return ResponseEntity.ok(roomTypeDto);
    }
    
    @GetMapping
    public ResponseEntity<List<RoomTypeDto>> getAllRoomTypes() {
        logger.info("Getting all room types");
        
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        List<RoomTypeDto> roomTypeDtos = roomTypes.stream()
                .map(roomTypeMapper::toDto)
                .collect(Collectors.toList());
        
        logger.info("Found {} room types", roomTypeDtos.size());
        return ResponseEntity.ok(roomTypeDtos);
    }
}
