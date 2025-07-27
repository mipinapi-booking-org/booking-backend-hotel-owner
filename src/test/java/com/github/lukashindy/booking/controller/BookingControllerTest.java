package com.github.lukashindy.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lukashindy.booking.dto.BookingDto;
import com.github.lukashindy.booking.model.Booking;
import com.github.lukashindy.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID hotelOwnerId = UUID.randomUUID();
    private final Long bookingId = 1L;

    @Test
    void getAllBookings_ShouldReturnBookingsList() throws Exception {
        // Given
        List<BookingDto> bookings = Arrays.asList(
                createBookingDto(1L, "CREATED"),
                createBookingDto(2L, "CONFIRMED")
        );
        when(bookingService.getAllBookings()).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/v1/bookings")
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("CREATED"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("CONFIRMED"));
    }

    @Test
    void getAllBookings_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(bookingService.getAllBookings()).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/v1/bookings")
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getBookingById_ShouldReturnBooking() throws Exception {
        // Given
        BookingDto booking = createBookingDto(bookingId, "CONFIRMED");
        when(bookingService.getBookingById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        mockMvc.perform(get("/api/v1/bookings/{id}", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void getBookingById_WhenNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(bookingService.getBookingById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/bookings/{id}", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingsByStatus_ShouldReturnBookingsList() throws Exception {
        // Given
        List<BookingDto> bookings = Arrays.asList(
                createBookingDto(1L, "CREATED"),
                createBookingDto(2L, "CREATED")
        );
        when(bookingService.getBookingsByStatus(Booking.Status.CREATED)).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/v1/bookings/status/{status}", "CREATED")
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value("CREATED"))
                .andExpect(jsonPath("$[1].status").value("CREATED"));
    }

    @Test
    void getBookingsByStatus_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(bookingService.getBookingsByStatus(any(Booking.Status.class)))
                .thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/v1/bookings/status/{status}", "CREATED")
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void confirmBooking_ShouldReturnConfirmedBooking() throws Exception {
        // Given
        BookingDto confirmedBooking = createBookingDto(bookingId, "CONFIRMED");
        when(bookingService.confirmBooking(bookingId, hotelOwnerId)).thenReturn(confirmedBooking);

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/confirm", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void confirmBooking_WhenBookingNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(bookingService.confirmBooking(bookingId, hotelOwnerId))
                .thenThrow(new IllegalArgumentException("Booking not found"));

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/confirm", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void confirmBooking_WhenInvalidState_ShouldReturnBadRequest() throws Exception {
        // Given
        when(bookingService.confirmBooking(bookingId, hotelOwnerId))
                .thenThrow(new IllegalStateException("Invalid state"));

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/confirm", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refuseBooking_ShouldReturnRefusedBooking() throws Exception {
        // Given
        BookingDto refusedBooking = createBookingDto(bookingId, "REFUSED");
        String reason = "Недоступны номера";
        BookingController.RefuseRequest refuseRequest = new BookingController.RefuseRequest(reason);
        
        when(bookingService.refuseBooking(bookingId, hotelOwnerId, reason)).thenReturn(refusedBooking);

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/refuse", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refuseRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("REFUSED"));
    }

    @Test
    void refuseBooking_WhenInvalidArgument_ShouldReturnBadRequest() throws Exception {
        // Given
        String reason = "Недоступны номера";
        BookingController.RefuseRequest refuseRequest = new BookingController.RefuseRequest(reason);
        
        when(bookingService.refuseBooking(bookingId, hotelOwnerId, reason))
                .thenThrow(new IllegalArgumentException("Invalid argument"));

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/refuse", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refuseRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void completeBooking_ShouldReturnCompletedBooking() throws Exception {
        // Given
        BookingDto completedBooking = createBookingDto(bookingId, "COMPLETED");
        when(bookingService.completeBooking(bookingId, hotelOwnerId)).thenReturn(completedBooking);

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/complete", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void completeBooking_WhenBookingNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(bookingService.completeBooking(bookingId, hotelOwnerId))
                .thenThrow(new IllegalArgumentException("Booking not found"));

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/complete", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelBooking_ShouldReturnCancelledBooking() throws Exception {
        // Given
        BookingDto cancelledBooking = createBookingDto(bookingId, "CANCELLED");
        when(bookingService.cancelBooking(bookingId, hotelOwnerId)).thenReturn(cancelledBooking);

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/cancel", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelBooking_WhenInvalidState_ShouldReturnBadRequest() throws Exception {
        // Given
        when(bookingService.cancelBooking(bookingId, hotelOwnerId))
                .thenThrow(new IllegalStateException("Invalid state"));

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/cancel", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsForUserHotels_ShouldReturnBookingsList() throws Exception {
        // Given
        List<BookingDto> bookings = Arrays.asList(
                createBookingDto(1L, "CREATED"),
                createBookingDto(2L, "CONFIRMED")
        );
        when(bookingService.getAllBookingsForUser(hotelOwnerId)).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/v1/bookings/my-hotels")
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getBookingsForUserHotels_WhenServiceThrowsException_ShouldReturnBadRequest() throws Exception {
        // Given
        when(bookingService.getAllBookingsForUser(hotelOwnerId))
                .thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/v1/bookings/my-hotels")
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByStatusForUser_ShouldReturnBookingsList() throws Exception {
        // Given
        List<BookingDto> bookings = Arrays.asList(
                createBookingDto(1L, "CREATED"),
                createBookingDto(2L, "CREATED")
        );
        when(bookingService.getBookingsByStatusForUser(Booking.Status.CREATED, hotelOwnerId))
                .thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/v1/bookings/my-hotels/status/{status}", "CREATED")
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value("CREATED"))
                .andExpect(jsonPath("$[1].status").value("CREATED"));
    }

    @Test
    void confirmBookingSecure_ShouldReturnConfirmedBooking() throws Exception {
        // Given
        BookingDto confirmedBooking = createBookingDto(bookingId, "CONFIRMED");
        when(bookingService.confirmBookingWithAccessControl(bookingId, hotelOwnerId))
                .thenReturn(confirmedBooking);

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/confirm/secure", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void confirmBookingSecure_WhenAccessDenied_ShouldReturnForbidden() throws Exception {
        // Given
        when(bookingService.confirmBookingWithAccessControl(bookingId, hotelOwnerId))
                .thenThrow(new SecurityException("Access denied"));

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/confirm/secure", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void refuseBookingSecure_ShouldReturnRefusedBooking() throws Exception {
        // Given
        BookingDto refusedBooking = createBookingDto(bookingId, "REFUSED");
        String reason = "Недоступны номера";
        BookingController.RefuseRequest refuseRequest = new BookingController.RefuseRequest(reason);
        
        when(bookingService.refuseBookingWithAccessControl(bookingId, hotelOwnerId, reason))
                .thenReturn(refusedBooking);

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/refuse/secure", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refuseRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("REFUSED"));
    }

    @Test
    void refuseBookingSecure_WhenAccessDenied_ShouldReturnForbidden() throws Exception {
        // Given
        String reason = "Недоступны номера";
        BookingController.RefuseRequest refuseRequest = new BookingController.RefuseRequest(reason);
        
        when(bookingService.refuseBookingWithAccessControl(bookingId, hotelOwnerId, reason))
                .thenThrow(new SecurityException("Access denied"));

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/refuse/secure", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refuseRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void completeBookingSecure_ShouldReturnCompletedBooking() throws Exception {
        // Given
        BookingDto completedBooking = createBookingDto(bookingId, "COMPLETED");
        when(bookingService.completeBookingWithAccessControl(bookingId, hotelOwnerId))
                .thenReturn(completedBooking);

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/complete/secure", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void completeBookingSecure_WhenAccessDenied_ShouldReturnForbidden() throws Exception {
        // Given
        when(bookingService.completeBookingWithAccessControl(bookingId, hotelOwnerId))
                .thenThrow(new SecurityException("Access denied"));

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/complete/secure", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelBookingSecure_ShouldReturnCancelledBooking() throws Exception {
        // Given
        BookingDto cancelledBooking = createBookingDto(bookingId, "CANCELLED");
        when(bookingService.cancelBookingWithAccessControl(bookingId, hotelOwnerId))
                .thenReturn(cancelledBooking);

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/cancel/secure", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelBookingSecure_WhenAccessDenied_ShouldReturnForbidden() throws Exception {
        // Given
        when(bookingService.cancelBookingWithAccessControl(bookingId, hotelOwnerId))
                .thenThrow(new SecurityException("Access denied"));

        // When & Then
        mockMvc.perform(patch("/api/v1/bookings/{id}/cancel/secure", bookingId)
                        .param("hotelOwnerId", hotelOwnerId.toString()))
                .andExpect(status().isForbidden());
    }

    private BookingDto createBookingDto(Long id, String status) {
        BookingDto dto = new BookingDto();
        dto.setId(id);
        dto.setStatus(status);
        dto.setCheckInDate(LocalDateTime.now().plusDays(1));
        dto.setCheckOutDate(LocalDateTime.now().plusDays(3));
        dto.setGuestFullNames("Test Guest");
        dto.setSpecialRequests("No special requests");
        return dto;
    }
}
