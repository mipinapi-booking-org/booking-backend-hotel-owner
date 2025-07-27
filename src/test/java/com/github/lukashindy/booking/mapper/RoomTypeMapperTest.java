package com.github.lukashindy.booking.mapper;

import com.github.lukashindy.booking.dto.RoomTypeDto;
import com.github.lukashindy.booking.model.RoomType;
import com.github.lukashindy.booking.model.Hotel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class RoomTypeMapperTest {

    private static final Logger logger = LoggerFactory.getLogger(RoomTypeMapperTest.class);
    private final RoomTypeMapper mapper = Mappers.getMapper(RoomTypeMapper.class);

    @Test
    void testToDto() {
        logger.info("Starting testToDto test");
        
        // Given
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        RoomType roomType = new RoomType();
        roomType.setId(10L);
        roomType.setName("Deluxe");
        roomType.setCapacity(2);
        roomType.setHotel(hotel);

        // When
        RoomTypeDto dto = mapper.toDto(roomType);

        // Then
        assertNotNull(dto);
        assertEquals(roomType.getId(), dto.getId());
        assertEquals(roomType.getName(), dto.getName());
        assertEquals(roomType.getCapacity(), dto.getCapacity());
        assertEquals(roomType.getHotel().getId(), dto.getHotelId());
        
        logger.info("Completed testToDto test successfully");
    }

    @Test
    void testToEntity() {
        logger.info("Starting testToEntity test");
        
        // Given
        RoomTypeDto dto = new RoomTypeDto();
        dto.setId(10L);
        dto.setName("Deluxe");
        dto.setCapacity(2);
        dto.setHotelId(1L);

        // When
        RoomType roomType = mapper.toEntity(dto);

        // Then
        assertNotNull(roomType);
        assertEquals(dto.getId(), roomType.getId());
        assertEquals(dto.getName(), roomType.getName());
        assertEquals(dto.getCapacity(), roomType.getCapacity());
        assertEquals(dto.getHotelId(), roomType.getHotel().getId());
        
        logger.info("Completed testToEntity test successfully");
    }

    @Test
    void testMapHotel() {
        logger.info("Starting testMapHotel test");
        
        // Given
        Long hotelId = 5L;

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
