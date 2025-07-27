package com.github.lukashindy.booking.mapper;

import com.github.lukashindy.booking.dto.RoomDto;
import com.github.lukashindy.booking.model.Room;
import com.github.lukashindy.booking.model.RoomType;
import com.github.lukashindy.booking.model.Hotel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class RoomMapperTest {

    private static final Logger logger = LoggerFactory.getLogger(RoomMapperTest.class);
    private final RoomMapper mapper = Mappers.getMapper(RoomMapper.class);

    @Test
    void testToDto() {
        logger.info("Starting testToDto test");
        
        // Given
        RoomType roomType = new RoomType();
        roomType.setId(1L);

        Hotel hotel = new Hotel();
        hotel.setId(2L);

        Room room = new Room();
        room.setId(10L);
        room.setRoomNumber("101");
        room.setRoomType(roomType);
        room.setHotel(hotel);

        // When
        RoomDto dto = mapper.toDto(room);

        // Then
        assertNotNull(dto);
        assertEquals(room.getId(), dto.getId());
        assertEquals(room.getRoomNumber(), dto.getRoomNumber());
        assertEquals(room.getRoomType().getId(), dto.getRoomTypeId());
        assertEquals(room.getHotel().getId(), dto.getHotelId());
        
        logger.info("Completed testToDto test successfully");
    }

    @Test
    void testToEntity() {
        logger.info("Starting testToEntity test");
        
        // Given
        RoomDto dto = new RoomDto();
        dto.setId(10L);
        dto.setRoomNumber("101");
        dto.setRoomTypeId(1L);
        dto.setHotelId(2L);

        // When
        Room room = mapper.toEntity(dto);

        // Then
        assertNotNull(room);
        assertEquals(dto.getId(), room.getId());
        assertEquals(dto.getRoomNumber(), room.getRoomNumber());
        assertEquals(dto.getRoomTypeId(), room.getRoomType().getId());
        assertEquals(dto.getHotelId(), room.getHotel().getId());
        
        logger.info("Completed testToEntity test successfully");
    }

    @Test
    void testMapRoomType() {
        logger.info("Starting testMapRoomType test");
        
        // Given
        Long roomTypeId = 5L;

        // When
        RoomType roomType = mapper.mapRoomType(roomTypeId);

        // Then
        assertNotNull(roomType);
        assertEquals(roomTypeId, roomType.getId());
        
        logger.info("Completed testMapRoomType test successfully");
    }

    @Test
    void testMapRoomTypeNull() {
        logger.info("Starting testMapRoomTypeNull test");
        
        // Given
        Long roomTypeId = null;

        // When
        RoomType roomType = mapper.mapRoomType(roomTypeId);

        // Then
        assertNull(roomType);
        
        logger.info("Completed testMapRoomTypeNull test successfully");
    }

    @Test
    void testMapHotel() {
        logger.info("Starting testMapHotel test");
        
        // Given
        Long hotelId = 3L;

        // When
        Hotel hotel = mapper.mapHotel(hotelId);

        // Then
        assertNotNull(hotel);
        assertEquals(hotelId, hotel.getId());
        
        logger.info("Completed testMapHotel test successfully");
    }

    @Test
    void testMapHotelNull() {
        logger.info("Starting testMapHotelNull test");
        
        // Given
        Long hotelId = null;

        // When
        Hotel hotel = mapper.mapHotel(hotelId);

        // Then
        assertNull(hotel);
        
        logger.info("Completed testMapHotelNull test successfully");
    }
}
