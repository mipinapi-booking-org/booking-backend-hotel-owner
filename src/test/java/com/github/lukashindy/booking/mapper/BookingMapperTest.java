package com.github.lukashindy.booking.mapper;

import com.github.lukashindy.booking.dto.BookingDto;
import com.github.lukashindy.booking.model.Booking;
import com.github.lukashindy.booking.model.Room;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private static final Logger logger = LoggerFactory.getLogger(BookingMapperTest.class);
    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void testToDto() {
        logger.info("Starting testToDto test");
        
        // Given
        Room room = new Room();
        room.setId(1L);

        Booking booking = new Booking();
        booking.setId(10L);
        booking.setRoom(room);
        booking.setClientId(UUID.randomUUID());
        booking.setCheckInDate(LocalDateTime.of(2025, 8, 1, 14, 0));
        booking.setCheckOutDate(LocalDateTime.of(2025, 8, 5, 12, 0));
        booking.setGuestFullNames("John Doe, Jane Doe");
        booking.setSpecialRequests("Late check-in");
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.setRefuseReason(null);

        // When
        BookingDto dto = mapper.toDto(booking);

        // Then
        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getRoom().getId(), dto.getRoomId());
        assertEquals(booking.getClientId(), dto.getClientId());
        assertEquals(booking.getCheckInDate(), dto.getCheckInDate());
        assertEquals(booking.getCheckOutDate(), dto.getCheckOutDate());
        assertEquals(booking.getGuestFullNames(), dto.getGuestFullNames());
        assertEquals(booking.getSpecialRequests(), dto.getSpecialRequests());
        assertEquals(booking.getStatus().name(), dto.getStatus());
        assertEquals(booking.getRefuseReason(), dto.getRefuseReason());
        
        logger.info("Completed testToDto test successfully");
    }

    @Test
    void testToEntity() {
        logger.info("Starting testToEntity test");
        
        // Given
        BookingDto dto = new BookingDto();
        dto.setId(10L);
        dto.setRoomId(1L);
        dto.setClientId(UUID.randomUUID());
        dto.setCheckInDate(LocalDateTime.of(2025, 8, 1, 14, 0));
        dto.setCheckOutDate(LocalDateTime.of(2025, 8, 5, 12, 0));
        dto.setGuestFullNames("John Doe, Jane Doe");
        dto.setSpecialRequests("Late check-in");
        dto.setStatus("CONFIRMED");
        dto.setRefuseReason("Fully booked");

        // When
        Booking entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getClientId(), entity.getClientId());
        assertEquals(dto.getCheckInDate(), entity.getCheckInDate());
        assertEquals(dto.getCheckOutDate(), entity.getCheckOutDate());
        assertEquals(dto.getGuestFullNames(), entity.getGuestFullNames());
        assertEquals(dto.getSpecialRequests(), entity.getSpecialRequests());
        assertEquals(Booking.Status.valueOf(dto.getStatus()), entity.getStatus());
        assertEquals(dto.getRefuseReason(), entity.getRefuseReason());
        // Room will be created with the provided ID
        assertNotNull(entity.getRoom());
        assertEquals(dto.getRoomId(), entity.getRoom().getId());
        
        logger.info("Completed testToEntity test successfully");
    }

    @Test
    void testMapRoomId() {
        logger.info("Starting testMapRoomId test");
        
        // When
        Room room = mapper.mapRoom(5L);

        // Then
        assertNotNull(room);
        assertEquals(5L, room.getId());
        
        logger.info("Completed testMapRoomId test successfully");
    }

    @Test
    void testMapRoomIdNull() {
        logger.info("Starting testMapRoomIdNull test");
        
        // Given
        Long roomId = null;

        // When
        Room room = mapper.mapRoom(roomId);

        // Then
        assertNull(room);
        
        logger.info("Completed testMapRoomIdNull test successfully");
    }
}
