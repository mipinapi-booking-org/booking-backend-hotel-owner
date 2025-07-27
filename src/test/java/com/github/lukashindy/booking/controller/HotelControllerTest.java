package com.github.lukashindy.booking.controller;

import com.github.lukashindy.booking.dto.HotelDto;
import com.github.lukashindy.booking.exception.ResourceNotFoundException;
import com.github.lukashindy.booking.mapper.HotelMapper;
import com.github.lukashindy.booking.model.Hotel;
import com.github.lukashindy.booking.model.HotelOwner;
import com.github.lukashindy.booking.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelControllerTest {
    
    private static final Logger logger = LoggerFactory.getLogger(HotelControllerTest.class);
    
    @Mock
    private HotelRepository hotelRepository;
    
    @Mock
    private HotelMapper hotelMapper;
    
    @InjectMocks
    private HotelController hotelController;
    
    private Hotel testHotel;
    private HotelDto testHotelDto;
    private HotelOwner testOwner;
    
    @BeforeEach
    void setUp() {
        logger.info("Setting up test data for HotelControllerTest");
        
        // Создаем тестового владельца отеля
        testOwner = new HotelOwner();
        testOwner.setId(1L);
        testOwner.setName("John Smith");
        testOwner.setEmail("admin@grandhotel.com");
        testOwner.setRole("ADMIN");
        
        // Создаем тестовый отель
        testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Grand Paradise Hotel");
        testHotel.setCountry("Maldives");
        testHotel.setCity("Male");
        testHotel.setStreet("Paradise Island Resort");
        testHotel.setOwner(testOwner);
        
        // Создаем тестовый DTO
        testHotelDto = new HotelDto();
        testHotelDto.setId(1L);
        testHotelDto.setName("Grand Paradise Hotel");
        testHotelDto.setCountry("Maldives");
        testHotelDto.setCity("Male");
        testHotelDto.setStreet("Paradise Island Resort");
        testHotelDto.setOwnerId(1L);
        
        logger.info("Test data setup completed");
    }
    
    @Test
    void getAllHotels_ShouldReturnListOfHotels() {
        logger.info("Testing getAllHotels method");
        
        // Given
        List<Hotel> hotels = Arrays.asList(testHotel);
        
        when(hotelRepository.findAll()).thenReturn(hotels);
        when(hotelMapper.toDto(any(Hotel.class))).thenReturn(testHotelDto);
        
        // When
        ResponseEntity<List<HotelDto>> response = hotelController.getAllHotels();
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<HotelDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(testHotelDto.getName(), responseBody.get(0).getName());
        
        verify(hotelRepository, times(1)).findAll();
        verify(hotelMapper, times(1)).toDto(testHotel);
        
        logger.info("getAllHotels test completed successfully");
    }
    
    @Test
    void getAllHotels_ShouldReturnEmptyList_WhenNoHotelsExist() {
        logger.info("Testing getAllHotels method with empty result");
        
        // Given
        when(hotelRepository.findAll()).thenReturn(Arrays.asList());
        
        // When
        ResponseEntity<List<HotelDto>> response = hotelController.getAllHotels();
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<HotelDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isEmpty());
        
        verify(hotelRepository, times(1)).findAll();
        verify(hotelMapper, never()).toDto(any(Hotel.class));
        
        logger.info("getAllHotels empty test completed successfully");
    }
    
    @Test
    void getHotelById_ShouldReturnHotel_WhenHotelExists() {
        logger.info("Testing getHotelById method with existing hotel");
        
        // Given
        Long hotelId = 1L;
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(testHotel));
        when(hotelMapper.toDto(testHotel)).thenReturn(testHotelDto);
        
        // When
        ResponseEntity<HotelDto> response = hotelController.getHotelById(hotelId);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        HotelDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(testHotelDto.getName(), responseBody.getName());
        assertEquals(testHotelDto.getId(), responseBody.getId());
        
        verify(hotelRepository, times(1)).findById(hotelId);
        verify(hotelMapper, times(1)).toDto(testHotel);
        
        logger.info("getHotelById existing hotel test completed successfully");
    }
    
    @Test
    void getHotelById_ShouldReturnNotFound_WhenHotelDoesNotExist() {
        logger.info("Testing getHotelById method with non-existing hotel");
        
        // Given
        Long hotelId = 999L;
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> hotelController.getHotelById(hotelId));
    }
}
