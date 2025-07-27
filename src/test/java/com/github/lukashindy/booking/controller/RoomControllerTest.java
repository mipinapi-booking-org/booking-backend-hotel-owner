package com.github.lukashindy.booking.controller;

import com.github.lukashindy.booking.dto.RoomDto;
import com.github.lukashindy.booking.dto.RoomWithDetailsDto;
import com.github.lukashindy.booking.mapper.RoomMapper;
import com.github.lukashindy.booking.model.Hotel;
import com.github.lukashindy.booking.model.Room;
import com.github.lukashindy.booking.model.RoomType;
import com.github.lukashindy.booking.repository.RoomRepository;
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
class RoomControllerTest {
    
    private static final Logger logger = LoggerFactory.getLogger(RoomControllerTest.class);
    
    @Mock
    private RoomRepository roomRepository;
    
    @Mock
    private RoomMapper roomMapper;
    
    @InjectMocks
    private RoomController roomController;
    
    private Room testRoom;
    private RoomDto testRoomDto;
    private RoomWithDetailsDto testRoomWithDetailsDto;
    private Hotel testHotel;
    private RoomType testRoomType;
    
    @BeforeEach
    void setUp() {
        logger.info("Setting up test data for RoomControllerTest");
        
        // Создаем тестовый отель
        testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Grand Paradise Hotel");
        testHotel.setCity("Male");
        testHotel.setCountry("Maldives");
        
        // Создаем тестовый тип комнаты
        testRoomType = new RoomType();
        testRoomType.setId(1L);
        testRoomType.setName("Standard");
        testRoomType.setCapacity(2);
        testRoomType.setHotel(testHotel);
        
        // Создаем тестовую комнату
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setRoomNumber("ST-001");
        testRoom.setRoomType(testRoomType);
        testRoom.setHotel(testHotel);
        
        // Создаем тестовый базовый DTO
        testRoomDto = new RoomDto();
        testRoomDto.setId(1L);
        testRoomDto.setRoomNumber("ST-001");
        testRoomDto.setRoomTypeId(1L);
        testRoomDto.setHotelId(1L);
        
        // Создаем тестовый расширенный DTO
        testRoomWithDetailsDto = new RoomWithDetailsDto();
        testRoomWithDetailsDto.setId(1L);
        testRoomWithDetailsDto.setRoomNumber("ST-001");
        testRoomWithDetailsDto.setRoomTypeId(1L);
        testRoomWithDetailsDto.setRoomTypeName("Standard");
        testRoomWithDetailsDto.setRoomTypeCapacity(2);
        testRoomWithDetailsDto.setHotelId(1L);
        testRoomWithDetailsDto.setHotelName("Grand Paradise Hotel");
        testRoomWithDetailsDto.setHotelCity("Male");
        testRoomWithDetailsDto.setHotelCountry("Maldives");
        
        logger.info("Test data setup completed");
    }
    
    @Test
    void getRoomsByHotelId_ShouldReturnListOfRoomsWithDetails() {
        logger.info("Testing getRoomsByHotelId method");
        
        // Given
        Long hotelId = 1L;
        List<Room> rooms = Arrays.asList(testRoom);
        
        when(roomRepository.findByHotelId(hotelId)).thenReturn(rooms);
        when(roomMapper.toDetailedDto(any(Room.class))).thenReturn(testRoomWithDetailsDto);
        
        // When
        ResponseEntity<List<RoomWithDetailsDto>> response = roomController.getRoomsByHotelId(hotelId);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<RoomWithDetailsDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(testRoomWithDetailsDto.getRoomNumber(), responseBody.get(0).getRoomNumber());
        assertEquals(testRoomWithDetailsDto.getHotelName(), responseBody.get(0).getHotelName());
        assertEquals(testRoomWithDetailsDto.getRoomTypeName(), responseBody.get(0).getRoomTypeName());
        
        verify(roomRepository, times(1)).findByHotelId(hotelId);
        verify(roomMapper, times(1)).toDetailedDto(testRoom);
        
        logger.info("getRoomsByHotelId test completed successfully");
    }
    
    @Test
    void getRoomsByHotelId_ShouldReturnEmptyList_WhenNoRoomsExist() {
        logger.info("Testing getRoomsByHotelId method with empty result");
        
        // Given
        Long hotelId = 999L;
        when(roomRepository.findByHotelId(hotelId)).thenReturn(Arrays.asList());
        
        // When
        ResponseEntity<List<RoomWithDetailsDto>> response = roomController.getRoomsByHotelId(hotelId);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<RoomWithDetailsDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isEmpty());
        
        verify(roomRepository, times(1)).findByHotelId(hotelId);
        verify(roomMapper, never()).toDetailedDto(any(Room.class));
        
        logger.info("getRoomsByHotelId empty test completed successfully");
    }
    
    @Test
    void getRoomsByRoomTypeId_ShouldReturnListOfRoomsWithDetails() {
        logger.info("Testing getRoomsByRoomTypeId method");
        
        // Given
        Long roomTypeId = 1L;
        List<Room> rooms = Arrays.asList(testRoom);
        
        when(roomRepository.findByRoomTypeId(roomTypeId)).thenReturn(rooms);
        when(roomMapper.toDetailedDto(any(Room.class))).thenReturn(testRoomWithDetailsDto);
        
        // When
        ResponseEntity<List<RoomWithDetailsDto>> response = roomController.getRoomsByRoomTypeId(roomTypeId);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<RoomWithDetailsDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(testRoomWithDetailsDto.getRoomNumber(), responseBody.get(0).getRoomNumber());
        assertEquals(testRoomWithDetailsDto.getRoomTypeName(), responseBody.get(0).getRoomTypeName());
        
        verify(roomRepository, times(1)).findByRoomTypeId(roomTypeId);
        verify(roomMapper, times(1)).toDetailedDto(testRoom);
        
        logger.info("getRoomsByRoomTypeId test completed successfully");
    }
    
    @Test
    void getRoomById_ShouldReturnRoomWithDetails_WhenRoomExists() {
        logger.info("Testing getRoomById method with existing room");
        
        // Given
        Long roomId = 1L;
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(roomMapper.toDetailedDto(testRoom)).thenReturn(testRoomWithDetailsDto);
        
        // When
        ResponseEntity<RoomWithDetailsDto> response = roomController.getRoomById(roomId);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        RoomWithDetailsDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(testRoomWithDetailsDto.getRoomNumber(), responseBody.getRoomNumber());
        assertEquals(testRoomWithDetailsDto.getId(), responseBody.getId());
        assertEquals(testRoomWithDetailsDto.getHotelName(), responseBody.getHotelName());
        assertEquals(testRoomWithDetailsDto.getRoomTypeName(), responseBody.getRoomTypeName());
        
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomMapper, times(1)).toDetailedDto(testRoom);
        
        logger.info("getRoomById existing room test completed successfully");
    }
    
    @Test
    void getRoomById_ShouldReturnNotFound_WhenRoomDoesNotExist() {
        logger.info("Testing getRoomById method with non-existing room");
        
        // Given
        Long roomId = 999L;
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        
        // When
        ResponseEntity<RoomWithDetailsDto> response = roomController.getRoomById(roomId);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomMapper, never()).toDetailedDto(any(Room.class));
        
        logger.info("getRoomById non-existing room test completed successfully");
    }
    
    @Test
    void getAllRooms_ShouldReturnListOfBasicRooms() {
        logger.info("Testing getAllRooms method");
        
        // Given
        List<Room> rooms = Arrays.asList(testRoom);
        
        when(roomRepository.findAll()).thenReturn(rooms);
        when(roomMapper.toDto(any(Room.class))).thenReturn(testRoomDto);
        
        // When
        ResponseEntity<List<RoomDto>> response = roomController.getAllRooms();
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<RoomDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(testRoomDto.getRoomNumber(), responseBody.get(0).getRoomNumber());
        assertEquals(testRoomDto.getId(), responseBody.get(0).getId());
        
        verify(roomRepository, times(1)).findAll();
        verify(roomMapper, times(1)).toDto(testRoom);
        
        logger.info("getAllRooms test completed successfully");
    }
}
