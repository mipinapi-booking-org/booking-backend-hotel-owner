package com.github.lukashindy.booking.controller;

import com.github.lukashindy.booking.dto.HotelDto;
import com.github.lukashindy.booking.mapper.HotelMapper;
import com.github.lukashindy.booking.model.Hotel;
import com.github.lukashindy.booking.repository.HotelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {
    
    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @Autowired
    private HotelMapper hotelMapper;
    
    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        logger.info("Getting all hotels");
        
        List<Hotel> hotels = hotelRepository.findAll();
        List<HotelDto> hotelDtos = hotels.stream()
                .map(hotelMapper::toDto)
                .collect(Collectors.toList());
        
        logger.info("Found {} hotels", hotelDtos.size());
        return ResponseEntity.ok(hotelDtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id) {
        logger.info("Getting hotel with ID: {}", id);
        
        return hotelRepository.findById(id)
                .map(hotel -> {
                    HotelDto hotelDto = hotelMapper.toDto(hotel);
                    logger.info("Found hotel: {}", hotelDto.getName());
                    return ResponseEntity.ok(hotelDto);
                })
                .orElseGet(() -> {
                    logger.warn("Hotel with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
