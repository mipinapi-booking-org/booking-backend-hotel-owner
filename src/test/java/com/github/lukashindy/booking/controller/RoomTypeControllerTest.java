package com.github.lukashindy.booking.controller;

import com.github.lukashindy.booking.dto.RoomTypeDto;
import com.github.lukashindy.booking.mapper.RoomTypeMapper;
import com.github.lukashindy.booking.model.Hotel;
import com.github.lukashindy.booking.model.RoomType;
import com.github.lukashindy.booking.repository.RoomTypeRepository;
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
class RoomTypeControllerTest {
    
    private static final Logger logger = LoggerFactory.getLogger(RoomTypeControllerTest.class);
    
    @Mock
    private RoomTypeRepository roomTypeRepository;
    
    @Mock
    private RoomTypeMapper roomTypeMapper;
    
    @InjectMocks
    private RoomTypeController roomTypeController;
    
    private RoomType testRoomType;
    private RoomTypeDto testRoomTypeDto;
    private Hotel testHotel;
    
    @BeforeEach
    void setUp() {
        logger.info("Setting up test data for RoomTypeControllerTest");
        
        // Создаем тестовый отель
        testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Grand Paradise Hotel");
        
        // Создаем тестовый тип комнаты
        testRoomType = new RoomType();
        testRoomType.setId(1L);
        testRoomType.setName("Standard");
        testRoomType.setCapacity(2);
        testRoomType.setHotel(testHotel);
        
        // Создаем тестовый DTO
        testRoomTypeDto = new RoomTypeDto();
        testRoomTypeDto.setId(1L);
        testRoomTypeDto.setName("Standard");
        testRoomTypeDto.setCapacity(2);
        testRoomTypeDto.setHotelId(1L);
        
        logger.info("Test data setup completed");
    }
    
    @Test
    void getRoomTypesByHotelId_ShouldReturnListOfRoomTypes() {
        logger.info("Testing getRoomTypesByHotelId method");
        
        // Given
        Long hotelId = 1L;
        List<RoomType> roomTypes = Arrays.asList(testRoomType);
        
        when(roomTypeRepository.findByHotelId(hotelId)).thenReturn(roomTypes);
        when(roomTypeMapper.toDto(any(RoomType.class))).thenReturn(testRoomTypeDto);
        
        // When
        ResponseEntity<List<RoomTypeDto>> response = roomTypeController.getRoomTypesByHotelId(hotelId);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<RoomTypeDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(testRoomTypeDto.getName(), responseBody.get(0).getName());
        
        verify(roomTypeRepository, times(1)).findByHotelId(hotelId);
        verify(roomTypeMapper, times(1)).toDto(testRoomType);
        
        logger.info("getRoomTypesByHotelId test completed successfully");
    }
    
    @Test
    void getRoomTypesByHotelId_ShouldReturnEmptyList_WhenNoRoomTypesExist() {
        logger.info("Testing getRoomTypesByHotelId method with empty result");
        
        // Given
        Long hotelId = 999L;
        when(roomTypeRepository.findByHotelId(hotelId)).thenReturn(Arrays.asList());
        
        // When
        ResponseEntity<List<RoomTypeDto>> response = roomTypeController.getRoomTypesByHotelId(hotelId);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<RoomTypeDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isEmpty());
        
        verify(roomTypeRepository, times(1)).findByHotelId(hotelId);
        verify(roomTypeMapper, never()).toDto(any(RoomType.class));
        
        logger.info("getRoomTypesByHotelId empty test completed successfully");
    }
    
    @Test
    void getRoomTypeById_ShouldReturnRoomType_WhenRoomTypeExists() {
        logger.info("Testing getRoomTypeById method with existing room type");
        
        // Given
        Long roomTypeId = 1L;
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.of(testRoomType));
        when(roomTypeMapper.toDto(testRoomType)).thenReturn(testRoomTypeDto);
        
        // When
        ResponseEntity<RoomTypeDto> response = roomTypeController.getRoomTypeById(roomTypeId);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        RoomTypeDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(testRoomTypeDto.getName(), responseBody.getName());
        assertEquals(testRoomTypeDto.getId(), responseBody.getId());
        
        verify(roomTypeRepository, times(1)).findById(roomTypeId);
        verify(roomTypeMapper, times(1)).toDto(testRoomType);
        
        logger.info("getRoomTypeById existing room type test completed successfully");
    }
    
    @Test
    void getRoomTypeById_ShouldReturnNotFound_WhenRoomTypeDoesNotExist() {
        logger.info("Testing getRoomTypeById method with non-existing room type");
        
        // Given
        Long roomTypeId = 999L;
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.empty());
        
        // When
        ResponseEntity<RoomTypeDto> response = roomTypeController.getRoomTypeById(roomTypeId);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(roomTypeRepository, times(1)).findById(roomTypeId);
        verify(roomTypeMapper, never()).toDto(any(RoomType.class));
        
        logger.info("getRoomTypeById non-existing room type test completed successfully");
    }
    
    @Test
    void getAllRoomTypes_ShouldReturnListOfRoomTypes() {
        logger.info("Testing getAllRoomTypes method");
        
        // Given
        List<RoomType> roomTypes = Arrays.asList(testRoomType);
        
        when(roomTypeRepository.findAll()).thenReturn(roomTypes);
        when(roomTypeMapper.toDto(any(RoomType.class))).thenReturn(testRoomTypeDto);
        
        // When
        ResponseEntity<List<RoomTypeDto>> response = roomTypeController.getAllRoomTypes();
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<RoomTypeDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(testRoomTypeDto.getName(), responseBody.get(0).getName());
        
        verify(roomTypeRepository, times(1)).findAll();
        verify(roomTypeMapper, times(1)).toDto(testRoomType);
        
        logger.info("getAllRoomTypes test completed successfully");
    }
}
