package com.github.lukashindy.booking.service;

import com.github.lukashindy.booking.dto.BookingDto;
import com.github.lukashindy.booking.mapper.BookingMapper;
import com.github.lukashindy.booking.model.Booking;
import com.github.lukashindy.booking.model.HotelOwner;
import com.github.lukashindy.booking.model.Room;
import com.github.lukashindy.booking.repository.BookingRepository;
import com.github.lukashindy.booking.repository.HotelOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private HotelOwnerRepository hotelOwnerRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private BookingDto testBookingDto;
    private HotelOwner testHotelOwner;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        // Создаем тестовые данные
        testHotelOwner = new HotelOwner();
        testHotelOwner.setId(UUID.randomUUID());
        testHotelOwner.setName("Test Owner");
        testHotelOwner.setEmail("owner@test.com");

        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setRoomNumber("ST-001");

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setClientId(UUID.randomUUID());
        testBooking.setRoom(testRoom);
        testBooking.setCheckInDate(LocalDateTime.now().plusDays(1));
        testBooking.setCheckOutDate(LocalDateTime.now().plusDays(3));
        testBooking.setGuestFullNames("John Doe");
        testBooking.setStatus(Booking.Status.CREATED);

        testBookingDto = new BookingDto();
        testBookingDto.setId(1L);
        testBookingDto.setClientId(testBooking.getClientId());
        testBookingDto.setRoomId(1L);
        testBookingDto.setCheckInDate(testBooking.getCheckInDate());
        testBookingDto.setCheckOutDate(testBooking.getCheckOutDate());
        testBookingDto.setGuestFullNames("John Doe");
        testBookingDto.setStatus("CREATED");
    }

    @Test
    void getAllBookings_ShouldReturnListOfBookingDtos() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        List<BookingDto> bookingDtos = Arrays.asList(testBookingDto);
        
        when(bookingRepository.findAll()).thenReturn(bookings);
        when(bookingMapper.toDto(bookings)).thenReturn(bookingDtos);

        // When
        List<BookingDto> result = bookingService.getAllBookings();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBookingDto.getId(), result.get(0).getId());
        
        verify(bookingRepository).findAll();
        verify(bookingMapper).toDto(bookings);
    }

    @Test
    void getBookingById_ShouldReturnBookingDto_WhenBookingExists() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingMapper.toDto(testBooking)).thenReturn(testBookingDto);

        // When
        Optional<BookingDto> result = bookingService.getBookingById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBookingDto.getId(), result.get().getId());
        
        verify(bookingRepository).findById(1L);
        verify(bookingMapper).toDto(testBooking);
    }

    @Test
    void getBookingById_ShouldReturnEmpty_WhenBookingNotExists() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<BookingDto> result = bookingService.getBookingById(1L);

        // Then
        assertFalse(result.isPresent());
        
        verify(bookingRepository).findById(1L);
        verify(bookingMapper, never()).toDto(any(Booking.class));
    }

    @Test
    void confirmBooking_ShouldUpdateStatusToConfirmed() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(hotelOwnerRepository.findById(testHotelOwner.getId())).thenReturn(Optional.of(testHotelOwner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(bookingMapper.toDto(testBooking)).thenReturn(testBookingDto);

        // When
        BookingDto result = bookingService.confirmBooking(1L, testHotelOwner.getId());

        // Then
        assertNotNull(result);
        assertEquals(Booking.Status.CONFIRMED, testBooking.getStatus());
        assertEquals(testHotelOwner, testBooking.getUpdatedBy());
        assertNotNull(testBooking.getLastUpdatedDate());
        assertNull(testBooking.getRefuseReason());
        
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void confirmBooking_ShouldThrowException_WhenBookingNotFound() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> bookingService.confirmBooking(1L, testHotelOwner.getId()));
        
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void refuseBooking_ShouldUpdateStatusToRefused_WithReason() {
        // Given
        String refuseReason = "Номер не доступен";
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(hotelOwnerRepository.findById(testHotelOwner.getId())).thenReturn(Optional.of(testHotelOwner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(bookingMapper.toDto(testBooking)).thenReturn(testBookingDto);

        // When
        BookingDto result = bookingService.refuseBooking(1L, testHotelOwner.getId(), refuseReason);

        // Then
        assertNotNull(result);
        assertEquals(Booking.Status.REFUSED, testBooking.getStatus());
        assertEquals(refuseReason, testBooking.getRefuseReason());
        assertEquals(testHotelOwner, testBooking.getUpdatedBy());
        assertNotNull(testBooking.getLastUpdatedDate());
        
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void refuseBooking_ShouldThrowException_WhenReasonIsEmpty() {
        // Given - no stubbing needed as exception is thrown before repository calls

        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> bookingService.refuseBooking(1L, testHotelOwner.getId(), ""));
        
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void completeBooking_ShouldUpdateStatusToCompleted_WhenBookingIsConfirmed() {
        // Given
        testBooking.setStatus(Booking.Status.CONFIRMED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(hotelOwnerRepository.findById(testHotelOwner.getId())).thenReturn(Optional.of(testHotelOwner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(bookingMapper.toDto(testBooking)).thenReturn(testBookingDto);

        // When
        BookingDto result = bookingService.completeBooking(1L, testHotelOwner.getId());

        // Then
        assertNotNull(result);
        assertEquals(Booking.Status.COMPLETED, testBooking.getStatus());
        assertEquals(testHotelOwner, testBooking.getUpdatedBy());
        assertNotNull(testBooking.getLastUpdatedDate());
        
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void completeBooking_ShouldThrowException_WhenBookingIsNotConfirmed() {
        // Given
        testBooking.setStatus(Booking.Status.CREATED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(hotelOwnerRepository.findById(testHotelOwner.getId())).thenReturn(Optional.of(testHotelOwner));

        // When & Then
        assertThrows(IllegalStateException.class, 
            () -> bookingService.completeBooking(1L, testHotelOwner.getId()));
        
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancelBooking_ShouldUpdateStatusToCancelled() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(hotelOwnerRepository.findById(testHotelOwner.getId())).thenReturn(Optional.of(testHotelOwner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(bookingMapper.toDto(testBooking)).thenReturn(testBookingDto);

        // When
        BookingDto result = bookingService.cancelBooking(1L, testHotelOwner.getId());

        // Then
        assertNotNull(result);
        assertEquals(Booking.Status.CANCELLED, testBooking.getStatus());
        assertEquals(testHotelOwner, testBooking.getUpdatedBy());
        assertNotNull(testBooking.getLastUpdatedDate());
        
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void getBookingsByStatus_ShouldReturnFilteredBookings() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        List<BookingDto> bookingDtos = Arrays.asList(testBookingDto);
        
        when(bookingRepository.findByStatus(Booking.Status.CREATED)).thenReturn(bookings);
        when(bookingMapper.toDto(bookings)).thenReturn(bookingDtos);

        // When
        List<BookingDto> result = bookingService.getBookingsByStatus(Booking.Status.CREATED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBookingDto.getId(), result.get(0).getId());
        
        verify(bookingRepository).findByStatus(Booking.Status.CREATED);
        verify(bookingMapper).toDto(bookings);
    }

    @Test
    void validateStatusTransition_ShouldThrowException_ForInvalidTransition() {
        // Given
        testBooking.setStatus(Booking.Status.COMPLETED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(hotelOwnerRepository.findById(testHotelOwner.getId())).thenReturn(Optional.of(testHotelOwner));

        // When & Then
        assertThrows(IllegalStateException.class, 
            () -> bookingService.confirmBooking(1L, testHotelOwner.getId()));
        
        verify(bookingRepository, never()).save(any());
    }
}
