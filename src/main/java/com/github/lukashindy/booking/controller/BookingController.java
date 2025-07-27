package com.github.lukashindy.booking.controller;

import com.github.lukashindy.booking.dto.BookingDto;
import com.github.lukashindy.booking.model.Booking;
import com.github.lukashindy.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * Получить все бронирования
     */
    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        List<BookingDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Получить бронирование по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id) {
        Optional<BookingDto> booking = bookingService.getBookingById(id);
        return booking.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получить бронирования по статусу
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingDto>> getBookingsByStatus(@PathVariable Booking.Status status) {
        List<BookingDto> bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Подтвердить бронирование
     */
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<BookingDto> confirmBooking(
            @PathVariable Long id,
            @RequestParam UUID hotelOwnerId) {
        try {
            BookingDto booking = bookingService.confirmBooking(id, hotelOwnerId);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Отклонить бронирование с указанием причины
     */
    @PatchMapping("/{id}/refuse")
    public ResponseEntity<BookingDto> refuseBooking(
            @PathVariable Long id,
            @RequestParam UUID hotelOwnerId,
            @RequestBody RefuseRequest refuseRequest) {
        try {
            BookingDto booking = bookingService.refuseBooking(id, hotelOwnerId, refuseRequest.getReason());
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Завершить бронирование
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<BookingDto> completeBooking(
            @PathVariable Long id,
            @RequestParam UUID hotelOwnerId) {
        try {
            BookingDto booking = bookingService.completeBooking(id, hotelOwnerId);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Отменить бронирование
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancelBooking(
            @PathVariable Long id,
            @RequestParam UUID hotelOwnerId) {
        try {
            BookingDto booking = bookingService.cancelBooking(id, hotelOwnerId);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получить бронирования для отелей пользователя
     */
    @GetMapping("/my-hotels")
    public ResponseEntity<List<BookingDto>> getBookingsForUserHotels(@RequestParam UUID hotelOwnerId) {
        try {
            List<BookingDto> bookings = bookingService.getAllBookingsForUser(hotelOwnerId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получить бронирования по статусу для отелей пользователя
     */
    @GetMapping("/my-hotels/status/{status}")
    public ResponseEntity<List<BookingDto>> getBookingsByStatusForUser(
            @PathVariable Booking.Status status,
            @RequestParam UUID hotelOwnerId) {
        try {
            List<BookingDto> bookings = bookingService.getBookingsByStatusForUser(status, hotelOwnerId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Подтвердить бронирование с проверкой прав доступа
     */
    @PatchMapping("/{id}/confirm/secure")
    public ResponseEntity<BookingDto> confirmBookingSecure(
            @PathVariable Long id,
            @RequestParam UUID hotelOwnerId) {
        try {
            BookingDto booking = bookingService.confirmBookingWithAccessControl(id, hotelOwnerId);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build(); // Forbidden
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Отклонить бронирование с проверкой прав доступа
     */
    @PatchMapping("/{id}/refuse/secure")
    public ResponseEntity<BookingDto> refuseBookingSecure(
            @PathVariable Long id,
            @RequestParam UUID hotelOwnerId,
            @RequestBody RefuseRequest refuseRequest) {
        try {
            BookingDto booking = bookingService.refuseBookingWithAccessControl(id, hotelOwnerId, refuseRequest.getReason());
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build(); // Forbidden
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Завершить бронирование с проверкой прав доступа
     */
    @PatchMapping("/{id}/complete/secure")
    public ResponseEntity<BookingDto> completeBookingSecure(
            @PathVariable Long id,
            @RequestParam UUID hotelOwnerId) {
        try {
            BookingDto booking = bookingService.completeBookingWithAccessControl(id, hotelOwnerId);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build(); // Forbidden
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Отменить бронирование с проверкой прав доступа
     */
    @PatchMapping("/{id}/cancel/secure")
    public ResponseEntity<BookingDto> cancelBookingSecure(
            @PathVariable Long id,
            @RequestParam UUID hotelOwnerId) {
        try {
            BookingDto booking = bookingService.cancelBookingWithAccessControl(id, hotelOwnerId);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build(); // Forbidden
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DTO для запроса отклонения бронирования
     */
    public static class RefuseRequest {
        private String reason;

        public RefuseRequest() {}

        public RefuseRequest(String reason) {
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
