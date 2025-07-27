package com.github.lukashindy.booking.mapper;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lukashindy.booking.dto.HotelDto;
import com.github.lukashindy.booking.mapper.HotelMapper;
import com.github.lukashindy.booking.model.Hotel;
import com.github.lukashindy.booking.model.HotelOwner;

import static org.junit.jupiter.api.Assertions.*;

class HotelMapperTest {

    private static final Logger logger = LoggerFactory.getLogger(HotelMapperTest.class);
    private final HotelMapper mapper = Mappers.getMapper(HotelMapper.class);

    @Test
    void testToDto() {
        logger.info("Starting testToDto test");
        
        // Given
        HotelOwner owner = new HotelOwner();
        owner.setId(UUID.randomUUID());

        Hotel hotel = new Hotel();
        hotel.setId(10L);
        hotel.setName("Grand Hotel");
        hotel.setCountry("USA");
        hotel.setCity("New York");
        hotel.setStreet("Broadway 123");

        // When
        HotelDto dto = mapper.toDto(hotel);

        // Then
        assertNotNull(dto);
        assertEquals(hotel.getId(), dto.getId());
        assertEquals(hotel.getName(), dto.getName());
        assertEquals(hotel.getCountry(), dto.getCountry());
        assertEquals(hotel.getCity(), dto.getCity());
        assertEquals(hotel.getStreet(), dto.getStreet());
        // Owner теперь не является частью Hotel - используется HotelOwnerAccess
        assertNull(dto.getOwnerId());
        
        logger.info("Completed testToDto test successfully");
    }

    @Test
    void testToEntity() {
        logger.info("Starting testToEntity test");
        
        // Given
        HotelDto dto = new HotelDto();
        dto.setId(10L);
        dto.setName("Grand Hotel");
        dto.setCountry("USA");
        dto.setCity("New York");
        dto.setStreet("Broadway 123");
        dto.setOwnerId(UUID.randomUUID());

        // When
        Hotel hotel = mapper.toEntity(dto);

        // Then
        assertNotNull(hotel);
        assertEquals(dto.getId(), hotel.getId());
        assertEquals(dto.getName(), hotel.getName());
        assertEquals(dto.getCountry(), hotel.getCountry());
        assertEquals(dto.getCity(), hotel.getCity());
        assertEquals(dto.getStreet(), hotel.getStreet());
        // Owner теперь не является частью Hotel - используется HotelOwnerAccess
        // assertEquals(dto.getOwnerId(), hotel.getOwner().getId());
        
        logger.info("Completed testToEntity test successfully");
    }
}
