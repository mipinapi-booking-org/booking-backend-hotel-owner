package com.github.lukashindy.booking.loader;

import com.github.lukashindy.booking.model.Hotel;
import com.github.lukashindy.booking.model.HotelOwner;
import com.github.lukashindy.booking.model.Room;
import com.github.lukashindy.booking.model.RoomType;
import com.github.lukashindy.booking.repository.HotelOwnerRepository;
import com.github.lukashindy.booking.repository.HotelRepository;
import com.github.lukashindy.booking.repository.RoomRepository;
import com.github.lukashindy.booking.repository.RoomTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataLoaderTest {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoaderTest.class);
    
    @Mock
    private HotelOwnerRepository hotelOwnerRepository;
    
    @Mock
    private HotelRepository hotelRepository;
    
    @Mock
    private RoomTypeRepository roomTypeRepository;
    
    @Mock
    private RoomRepository roomRepository;
    
    @InjectMocks
    private DataLoader dataLoader;
    
    private HotelOwner mockHotelOwner;
    private Hotel mockHotel;
    private RoomType mockRoomType;
    private Room mockRoom;
    
    @BeforeEach
    void setUp() {
        logger.info("Setting up test data for DataLoaderTest");
        
        // Создаем mock объекты
        mockHotelOwner = new HotelOwner();
        mockHotelOwner.setId(1L);
        mockHotelOwner.setName("John Smith");
        mockHotelOwner.setEmail("admin@grandhotel.com");
        mockHotelOwner.setRole("ADMIN");
        
        mockHotel = new Hotel();
        mockHotel.setId(1L);
        mockHotel.setName("Grand Paradise Hotel");
        mockHotel.setCountry("Maldives");
        mockHotel.setCity("Male");
        mockHotel.setOwner(mockHotelOwner);
        
        mockRoomType = new RoomType();
        mockRoomType.setId(1L);
        mockRoomType.setName("Standard");
        mockRoomType.setCapacity(2);
        mockRoomType.setHotel(mockHotel);
        
        mockRoom = new Room();
        mockRoom.setId(1L);
        mockRoom.setRoomNumber("ST-001");
        mockRoom.setRoomType(mockRoomType);
        mockRoom.setHotel(mockHotel);
        
        logger.info("Test data setup completed");
    }
    
    @Test
    void run_ShouldCreateCompleteDataStructure() throws Exception {
        logger.info("Testing DataLoader run method");
        
        // Given
        when(hotelOwnerRepository.save(any(HotelOwner.class))).thenReturn(mockHotelOwner);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(mockHotel);
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(mockRoomType);
        when(roomRepository.save(any(Room.class))).thenReturn(mockRoom);
        
        // When
        dataLoader.run();
        
        // Then
        // Проверяем, что создан один владелец отеля
        verify(hotelOwnerRepository, times(1)).save(any(HotelOwner.class));
        
        // Проверяем, что создан один отель
        verify(hotelRepository, times(1)).save(any(Hotel.class));
        
        // Проверяем, что создано 7 типов комнат (Standard, Standard Sea View, Deluxe, Comfort, King Size, Presidential, Economic)
        verify(roomTypeRepository, times(7)).save(any(RoomType.class));
        
        // Проверяем, что создано правильное количество комнат
        // (количество зависит от конкретной реализации getRoomCount)
        verify(roomRepository, times(70)).save(any(Room.class));
        
        logger.info("DataLoader run method test completed successfully");
    }
    
    @Test
    void run_ShouldCreateHotelOwnerWithCorrectData() throws Exception {
        logger.info("Testing hotel owner creation with correct data");
        
        // Given
        when(hotelOwnerRepository.save(any(HotelOwner.class))).thenReturn(mockHotelOwner);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(mockHotel);
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(mockRoomType);
        when(roomRepository.save(any(Room.class))).thenReturn(mockRoom);
        
        // When
        dataLoader.run();
        
        // Then
        ArgumentCaptor<HotelOwner> hotelOwnerCaptor = ArgumentCaptor.forClass(HotelOwner.class);
        verify(hotelOwnerRepository).save(hotelOwnerCaptor.capture());
        
        HotelOwner savedHotelOwner = hotelOwnerCaptor.getValue();
        assertEquals("John Smith", savedHotelOwner.getName());
        assertEquals("admin@grandhotel.com", savedHotelOwner.getEmail());
        assertEquals("ADMIN", savedHotelOwner.getRole());
        assertNotNull(savedHotelOwner.getPasswordHash());
        
        logger.info("Hotel owner creation test completed successfully");
    }
    
    @Test
    void run_ShouldCreateHotelWithCorrectData() throws Exception {
        logger.info("Testing hotel creation with correct data");
        
        // Given
        when(hotelOwnerRepository.save(any(HotelOwner.class))).thenReturn(mockHotelOwner);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(mockHotel);
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(mockRoomType);
        when(roomRepository.save(any(Room.class))).thenReturn(mockRoom);
        
        // When
        dataLoader.run();
        
        // Then
        ArgumentCaptor<Hotel> hotelCaptor = ArgumentCaptor.forClass(Hotel.class);
        verify(hotelRepository).save(hotelCaptor.capture());
        
        Hotel savedHotel = hotelCaptor.getValue();
        assertEquals("Grand Paradise Hotel", savedHotel.getName());
        assertEquals("Maldives", savedHotel.getCountry());
        assertEquals("Male", savedHotel.getCity());
        assertEquals("Paradise Island Resort, North Male Atoll", savedHotel.getStreet());
        assertNotNull(savedHotel.getOwner());
        
        logger.info("Hotel creation test completed successfully");
    }
    
    @Test
    void run_ShouldCreateAllRoomTypesWithCorrectNames() throws Exception {
        logger.info("Testing room types creation with correct names");
        
        // Given
        when(hotelOwnerRepository.save(any(HotelOwner.class))).thenReturn(mockHotelOwner);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(mockHotel);
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(mockRoomType);
        when(roomRepository.save(any(Room.class))).thenReturn(mockRoom);
        
        // When
        dataLoader.run();
        
        // Then
        ArgumentCaptor<RoomType> roomTypeCaptor = ArgumentCaptor.forClass(RoomType.class);
        verify(roomTypeRepository, times(7)).save(roomTypeCaptor.capture());
        
        List<RoomType> savedRoomTypes = roomTypeCaptor.getAllValues();
        String[] expectedRoomTypeNames = {
            "Standard", "Standard Sea View", "Deluxe", "Comfort", "King Size", "Presidential", "Economic"
        };
        
        for (int i = 0; i < expectedRoomTypeNames.length; i++) {
            assertEquals(expectedRoomTypeNames[i], savedRoomTypes.get(i).getName());
            assertNotNull(savedRoomTypes.get(i).getCapacity());
            assertNotNull(savedRoomTypes.get(i).getHotel());
        }
        
        logger.info("Room types creation test completed successfully");
    }
    
    @Test
    void run_ShouldCreateRoomsWithCorrectNamingPattern() throws Exception {
        logger.info("Testing rooms creation with correct naming pattern");
        
        // Given
        when(hotelOwnerRepository.save(any(HotelOwner.class))).thenReturn(mockHotelOwner);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(mockHotel);
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(mockRoomType);
        when(roomRepository.save(any(Room.class))).thenReturn(mockRoom);
        
        // When
        dataLoader.run();
        
        // Then
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository, times(70)).save(roomCaptor.capture());
        
        List<Room> savedRooms = roomCaptor.getAllValues();
        
        // Проверяем, что все комнаты имеют правильный формат номера (XX-###)
        for (Room room : savedRooms) {
            String roomNumber = room.getRoomNumber();
            assertNotNull(roomNumber);
            assertTrue(roomNumber.matches("[A-Z]{2}-\\d{3}"), 
                "Room number should match pattern XX-### but was: " + roomNumber);
            assertNotNull(room.getRoomType());
            assertNotNull(room.getHotel());
        }
        
        // Проверяем, что первые 10 комнат имеют префикс ST (Standard)
        for (int i = 0; i < 10; i++) {
            assertTrue(savedRooms.get(i).getRoomNumber().startsWith("ST-"),
                "First 10 rooms should have ST- prefix");
        }
        
        logger.info("Rooms creation test completed successfully");
    }
}
