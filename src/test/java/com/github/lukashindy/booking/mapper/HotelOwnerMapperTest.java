package com.github.lukashindy.booking.mapper;

import com.github.lukashindy.booking.dto.HotelOwnerDto;
import com.github.lukashindy.booking.model.HotelOwner;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class HotelOwnerMapperTest {

    private static final Logger logger = LoggerFactory.getLogger(HotelOwnerMapperTest.class);
    private final HotelOwnerMapper mapper = Mappers.getMapper(HotelOwnerMapper.class);

    @Test
    void testToDto() {
        logger.info("Starting testToDto test");
        
        // Given
        HotelOwner hotelOwner = new HotelOwner();
        hotelOwner.setId(1L);
        hotelOwner.setName("John Smith");
        hotelOwner.setEmail("john.smith@example.com");
        hotelOwner.setPasswordHash("hashedPassword123");
        hotelOwner.setRole("OWNER");

        // When
        HotelOwnerDto dto = mapper.toDto(hotelOwner);

        // Then
        assertNotNull(dto);
        assertEquals(hotelOwner.getId(), dto.getId());
        assertEquals(hotelOwner.getName(), dto.getName());
        assertEquals(hotelOwner.getEmail(), dto.getEmail());
        assertEquals(hotelOwner.getPasswordHash(), dto.getPasswordHash());
        assertEquals(hotelOwner.getRole(), dto.getRole());
        
        logger.info("Completed testToDto test successfully");
    }

    @Test
    void testToEntity() {
        logger.info("Starting testToEntity test");
        
        // Given
        HotelOwnerDto dto = new HotelOwnerDto();
        dto.setId(1L);
        dto.setName("John Smith");
        dto.setEmail("john.smith@example.com");
        dto.setPasswordHash("hashedPassword123");
        dto.setRole("OWNER");

        // When
        HotelOwner hotelOwner = mapper.toEntity(dto);

        // Then
        assertNotNull(hotelOwner);
        assertEquals(dto.getId(), hotelOwner.getId());
        assertEquals(dto.getName(), hotelOwner.getName());
        assertEquals(dto.getEmail(), hotelOwner.getEmail());
        assertEquals(dto.getPasswordHash(), hotelOwner.getPasswordHash());
        assertEquals(dto.getRole(), hotelOwner.getRole());
        
        logger.info("Completed testToEntity test successfully");
    }

    @Test
    void testToDtoWithNullValues() {
        logger.info("Starting testToDtoWithNullValues test");
        
        // Given
        HotelOwner hotelOwner = new HotelOwner();
        hotelOwner.setId(1L);
        hotelOwner.setName("John Smith");
        // Оставляем другие поля null

        // When
        HotelOwnerDto dto = mapper.toDto(hotelOwner);

        // Then
        assertNotNull(dto);
        assertEquals(hotelOwner.getId(), dto.getId());
        assertEquals(hotelOwner.getName(), dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getPasswordHash());
        assertNull(dto.getRole());
        
        logger.info("Completed testToDtoWithNullValues test successfully");
    }

    @Test
    void testToEntityWithNullValues() {
        logger.info("Starting testToEntityWithNullValues test");
        
        // Given
        HotelOwnerDto dto = new HotelOwnerDto();
        dto.setId(1L);
        dto.setName("John Smith");
        // Оставляем другие поля null

        // When
        HotelOwner hotelOwner = mapper.toEntity(dto);

        // Then
        assertNotNull(hotelOwner);
        assertEquals(dto.getId(), hotelOwner.getId());
        assertEquals(dto.getName(), hotelOwner.getName());
        assertNull(hotelOwner.getEmail());
        assertNull(hotelOwner.getPasswordHash());
        assertNull(hotelOwner.getRole());
        
        logger.info("Completed testToEntityWithNullValues test successfully");
    }
}
